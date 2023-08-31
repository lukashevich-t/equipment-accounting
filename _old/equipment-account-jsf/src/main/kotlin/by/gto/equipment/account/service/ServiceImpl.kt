package by.gto.equipment.account.service

//import javax.transaction.Transactional
import by.gto.equipment.account.dao.DaoJdbcJdbcUtils
import by.gto.equipment.account.e.ValueNotSpecifiedException
import by.gto.equipment.account.helpers.HibernateQueryResultDataSource
import by.gto.equipment.account.model.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.OutputStream
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.servlet.http.HttpSession
import javax.transaction.Transactional


//@Named
@ApplicationScoped
open class ServiceImpl {
//    private var eqStates: ReferenceAnswerJSON? = null
//    private var eqTypes: ReferenceAnswerJSON? = null
//    private var persons: ReferenceAnswerJSON? = null

    @Inject
    private lateinit var dao: DaoJdbcJdbcUtils

//    constructor() {
//        println("ServiceImpl constructor")
//    }

    @Throws(SQLException::class)
    open fun getEquipmentStates(): Map<Int, BaseReference> {
        var r = eqStates
        if (r == null) {
            r = dao.getEquipmentStates()
            eqStates = r
        }
        return r
    }

    @Throws(SQLException::class)
    open fun getEquipmentTypes(): Map<Int, BaseReference> {
        var r = eqTypes
        if (r == null) {
            r = dao.getEquipmentTypes()
            eqTypes = r
        }
        return r
    }

    @Throws(SQLException::class)
    open fun getResponsiblePersons(): Map<Int, BaseReference> {
        var r = persons
        if (r == null) {
            r = dao.getResponsiblePersons()
            persons = r
        }
        return r
    }

    //    public void setTxTemplate(TransactionTemplate txTemplate) {
    //        this.txTemplate = txTemplate;
    //    }

//    fun setDao(dao: DaoJdbcJdbcUtils) {
//        this.dao = dao
//    }

//    @Transactional(rollbackOn = arrayOf(Exception::class))
//    @Throws(SQLException::class)
//    open fun testTransactions() {
//        dao.testTransactions()
//    }


    @Throws(JRException::class, SQLException::class)
    open fun getInvNumbersFromGUIDs(sGuids: List<String>): List<InvNumberQR> {
        val nums = ArrayList<InvNumberQR>()

        for (eq in dao.getEquipmentsByGuidList(sGuids)) {
            nums.add(generateInvNumber(eq.invNumber, eq.purchaseDate, UUID.fromString(eq.guid)))
        }
        return nums
    }

    @Throws(WriterException::class, ValueNotSpecifiedException::class)
    open fun generateInvNumbers(date: LocalDate?, sRange: String): List<InvNumberQR> {
        if (StringUtils.isBlank(sRange)) {
            throw ValueNotSpecifiedException("must specify range")
        }
        val nums = ArrayList<InvNumberQR>()
        for (token in sRange.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            var matcher = patternRange.matcher(token)
            if (matcher.matches()) {
                for (i in Integer.parseInt(matcher.group(1))..Integer.parseInt(matcher.group(2))) {
                    nums.add(generateInvNumber(
                            StringUtils.leftPad(i.toString(), matcher.group(1).length, "0"),
                            date,
                            UUID.randomUUID()))
                }
            } else {
                matcher = patternMultiply.matcher(token)
                if (matcher.matches()) {
                    for (i in 0 until Integer.parseInt(matcher.group(2))) {
                        nums.add(generateInvNumber(
                                matcher.group(1),
                                date,
                                UUID.randomUUID()))
                    }
                } else {
                    nums.add(generateInvNumber(token.trim { it <= ' ' }.toUpperCase(), date, UUID.randomUUID()))
                }
            }
            //            String[] ends = token.split("-");
            //            if (ends.length == 1) {
            //                String s = ends[0].trim().toUpperCase();
            //                nums.add(generateInvNumber(s, date, UUID.randomUUID()));
            //            } else if (ends.length == 2) {
            //                try {
            //                    for (int i = Integer.parseInt(ends[0].trim()); i <= Integer.parseInt(ends[1].trim()); i++) {
            //                        nums.add(generateInvNumber(
            //                                StringUtils.leftPad(String.valueOf(i), ends[0].length(), "0"),
            //                                date,
            //                                UUID.randomUUID()));
            //                    }
            //                } catch (NumberFormatException e) {
            //                }
            //            }
        }
        return nums
    }

    @Throws(JRException::class)
    open fun writeNumbersToPdfStream(nums: List<InvNumberQR>, os: OutputStream, templateFilename: String = "invNumbers_A4_6x6.jasper") {
        val jasperPrint: JasperPrint
        val result = ArrayList<Array<Any?>>()
        for (item in nums) {
            result.add(arrayOf(item.invNumber, item.date, item.guid.toString(), item.image))
        }
        val ds = HibernateQueryResultDataSource(result, arrayOf("invNumber", "purchaseDate", "guid", "qr"))
        val input = this.javaClass.classLoader.getResourceAsStream(templateFilename)

        //HashMap<String, Object> reportParams = new HashMap<>();
        jasperPrint = JasperFillManager.fillReport(input, null, ds)
        JasperExportManager.exportReportToPdfStream(jasperPrint, os)
    }

    @Throws(WriterException::class)
    open fun generateInvNumber(invNum: String, date: LocalDate?, guid: UUID): InvNumberQR {
        val hintMap = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hintMap[EncodeHintType.MARGIN] = 0 /* default = 4 */
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

        val qrCodeWriter = QRCodeWriter()
        val myCodeText = String.format("%s\n%s\n%s", guid.toString(), invNum.toUpperCase().trim { it <= ' ' },
                if (date == null) "" else String.format("%1\$tY%1\$tm%1\$td", date))
        val byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hintMap)
        val CrunchifyWidth = byteMatrix.width
        val image = BufferedImage(CrunchifyWidth, CrunchifyWidth,
                BufferedImage.TYPE_INT_RGB)
        image.createGraphics()

        val graphics = image.graphics as Graphics2D
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth)
        graphics.color = Color.BLACK

        for (i in 0 until CrunchifyWidth) {
            for (j in 0 until CrunchifyWidth) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1)
                }
            }
        }

        //ImageIO.write(image, "png", new File("/var/qr" + invNum + ".png"));
        return InvNumberQR(invNum, guid, date, image)
    }

//    fun getEquipment(guid: UUID): Equipment? {
//        return dao.getEquipment(guid)
//    }

    open fun getEquipmentDescr(guid: UUID): EquipmentDescr? {
        return dao.getEquipmentDescr(guid)
    }

//    @Throws(Exception::class)
//    fun putEquipment(eq: Equipment): EquipmentDescr {
//        return dao.putEquipment(eq)
//    }

    open fun getCreateTimestamp() = createTimestamp

    open fun logEquipmentChange(eq: EquipmentDescr, action:Action, userId: Int, comment:String, date: LocalDateTime?=null) {
        dao.logEquipmentChange(eq, action, userId, comment, date)
    }

    @Transactional(rollbackOn = arrayOf(Exception::class))
    @Throws(Exception::class)
    open fun putEquipmentDescr(eqDescr: EquipmentDescr, userId: Int, failIfExists: Boolean): Array<Any?> {
        val oldEquipment = dao.getEquipmentDescr(UUID.fromString(eqDescr.guid))

        if (oldEquipment != null && failIfExists) {
            throw Exception("Такой guid уже зарегистрирован")
        }

        var typeId = dao.getIdByName("equipment_type", eqDescr.type!!)
        var stateId = dao.getIdByName("equipment_state", eqDescr.state!!)
        var personId = dao.getIdByName("responsible_person", eqDescr.person!!)
        var refsModified: Boolean? = false
        if (typeId == null || typeId <= 0) {
            typeId = dao.createEquipmentType(eqDescr.type!!)
            eqTypes = null
            refsModified = true
        }
        if (stateId == null || stateId <= 0) {
            stateId = dao.createEquipmentState(eqDescr.state!!)
            eqStates = null
            refsModified = true
        }
        if (personId == null || personId <= 0) {
            personId = dao.createPerson(eqDescr.person!!)
            persons = null
            refsModified = true
        }

        eqDescr.personId = personId
        eqDescr.stateId = stateId
        eqDescr.typeId = typeId
        val newEquipment = dao.putEquipment(eqDescr)
        val action: Action
        val comment: String?
        if (oldEquipment != null) {
            if (failIfExists) throw Exception("Такой guid уже зарегистрирован")
            action = Action.EQUIPMENT_MODIFY
            comment = newEquipment.diff(oldEquipment)
        } else {
            action = Action.EQUIPMENT_CREATE
            comment = newEquipment.toString()
        }
        dao.logEquipmentChange(newEquipment, action, userId, comment)
        return arrayOf(refsModified, newEquipment)
    }

    open fun loadUserInfo(userLogin_: String): UserInfo {
//        var userLogin = userLogin_
//        if (userLogin.toLowerCase().startsWith("cn=")) {
//            val parts = userLogin.split(",\\s?".toRegex(), 100).toTypedArray()
//            userLogin = parts[0].split("=".toRegex(), 3).toTypedArray()[1]
//        }

        val userInfoByLogin = dao.getUserInfoByLogin(userLogin_, userLogin_.toLowerCase().startsWith("cn="))
        // для админов сделаем, чтобы можно было запрашивать данные без ограничений по количеству запросов в минуту
        return userInfoByLogin
    }


    @Throws(SQLException::class)
    open fun searchEquipment(template: Equipment): MutableList<EquipmentDescr> {
        return dao.searchEquipment(template)
    }

    @Transactional(rollbackOn = arrayOf(Exception::class))
    @Throws(SQLException::class)
    open fun testTransaction() {
        dao.clearTestTable()
//        throw RecordNotFoundException("")
        dao.appendTestTable()
    }

    open fun getLog(guid: String) = LogEntries(dao.getLog(guid))

    open fun getCachedUserInfo(session: HttpSession, userLogin: String?): UserInfo {
        if (userLogin == null) return UserInfo()
        var result: UserInfo? = session.getAttribute(userLogin + "_userinfo") as UserInfo?
        if (null == result) {
            //            UserServiceImpl userService = (UserServiceImpl) ApplicationContextProvider.getBean("userService");
            result = loadUserInfo(userLogin)
            session.setAttribute(userLogin + "_userinfo", result)
        }
        return result
    }

//    fun putBlob(blobSize: Int, binSize: Int) {
//        dao.putBlob(blobSize, binSize)
//    }

    companion object {
        private val patternRange = Pattern.compile("^\\s*(\\d+)\\s*-\\s*(\\d+)\\s*$")
        private val patternMultiply = Pattern.compile("^\\s*([^*]+)\\*\\s*(\\d+)\\s*$")

        val QR_SIZE = 1000
        private val log = LoggerFactory.getLogger(ServiceImpl::class.java)

        //        private var persons:List<BaseReference>? = null
        private var persons: Map<Int, BaseReference>? = null
        private var eqStates: Map<Int, BaseReference>? = null
        private var eqTypes: Map<Int, BaseReference>? = null
        private val createTimestamp = System.currentTimeMillis()
    }

}
