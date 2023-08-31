package by.gto.equipment.account.service

import by.gto.equipment.account.e.ValueNotSpecifiedException
import by.gto.equipment.account.helpers.HibernateQueryResultDataSource
import by.gto.equipment.account.helpers.toGuid
import by.gto.equipment.account.helpers.toGuidBytes
import by.gto.equipment.account.model.Action
import by.gto.equipment.account.model.BaseReference
import by.gto.equipment.account.model.EQUIPMENT_ALREADY_EXISTS_MESSAGE
import by.gto.equipment.account.model.EquipmentDescr
import by.gto.equipment.account.model.EquipmentSearchTemplate
import by.gto.equipment.account.model.InvNumberQR
import by.gto.equipment.account.model.QR_SIZE
import by.gto.equipment.account.model.REF_EQUIPMENT_STATES_TABLE_NAME
import by.gto.equipment.account.model.REF_EQUIPMENT_TYPES_TABLE_NAME
import by.gto.equipment.account.model.REF_PERSONS_TABLE_NAME
import by.gto.equipment.account.model.UserInfo
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.OutputStream
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.EnumMap
import java.util.UUID
import java.util.regex.Pattern
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.transaction.Transactional
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext

@ApplicationScoped
class ServiceImpl {
    @Inject
    lateinit var mapper: by.gto.equipment.account.mappers.GlobalMapper

    @Throws(SQLException::class)
    fun loadEquipmentStates(): Map<Int, BaseReference> {
        var r = eqStates
        if (r == null) {
            r = mapper.loadEquipmentStates().associateBy({ it.id }, { it })
            eqStates = r
        }
        return r
    }

    @Throws(SQLException::class)
    fun loadEquipmentTypes(): Map<Int, BaseReference> {
        var r = eqTypes
        if (r == null) {
            r = mapper.loadEquipmentTypes().associateBy({ it.id }, { it })
            eqTypes = r
        }
        return r
    }

    @Throws(SQLException::class)
    fun loadResponsiblePersons(): Map<Int, BaseReference> {
        var r = persons
        if (r == null) {
            r = mapper.loadResponsiblePersons().associateBy({ it.id }, { it })
            persons = r
        }
        return r
    }

    @Throws(JRException::class, SQLException::class)
    fun loadInvNumbersFromGUIDs(sGuids: List<String>): List<InvNumberQR> =
        mapper.loadEquipmentsByGuidList(sGuids.map { it.toGuidBytes() })
            .map { generateInvNumber(it.invNumber, it.purchaseDate, it.guid.toGuid()) }

    @Throws(WriterException::class, ValueNotSpecifiedException::class)
    fun generateInvNumbers(date: LocalDate?, sRange: String): List<InvNumberQR> {
        if (sRange.isBlank()) {
            throw ValueNotSpecifiedException("must specify range")
        }
        val nums = ArrayList<InvNumberQR>()
        for (token in sRange.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            var matcher = patternRange.matcher(token)
            if (matcher.matches()) {
                for (i in Integer.parseInt(matcher.group(1))..Integer.parseInt(matcher.group(2))) {
                    nums.add(
                        generateInvNumber(
                            i.toString().padStart(matcher.group(1).length, '0'),
                            date,
                            UUID.randomUUID()
                        )
                    )
                }
            } else {
                matcher = patternMultiply.matcher(token)
                if (matcher.matches()) {
                    for (i in 0 until Integer.parseInt(matcher.group(2))) {
                        nums.add(
                            generateInvNumber(
                                matcher.group(1),
                                date,
                                UUID.randomUUID()
                            )
                        )
                    }
                } else {
                    nums.add(generateInvNumber(token.trim { it <= ' ' }.uppercase(), date, UUID.randomUUID()))
                }
            }
        }
        return nums
    }

    @Throws(JRException::class)
    fun writeNumbersToPdfStream(
        nums: List<InvNumberQR>,
        os: OutputStream,
        templateFilename: String = "a4_6x6.jasper"
    ) {
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
    fun generateInvNumber(invNum: String, date: LocalDate?, guid: UUID): InvNumberQR {
        val hintMap = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hintMap[EncodeHintType.MARGIN] = 0 /* default = 4 */
        hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

        val qrCodeWriter = QRCodeWriter()
        val myCodeText = String.format(
            "%s\n%s\n%s", guid.toString(), invNum.uppercase().trim { it <= ' ' },
            if (date == null) "" else String.format("%1\$tY%1\$tm%1\$td", date)
        )
        val byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hintMap)
        val crunchifyWidth = byteMatrix.width
        val image = BufferedImage(
            crunchifyWidth, crunchifyWidth,
            BufferedImage.TYPE_INT_RGB
        )
        image.createGraphics()

        val graphics = image.graphics as Graphics2D
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, crunchifyWidth, crunchifyWidth)
        graphics.color = Color.BLACK

        for (i in 0 until crunchifyWidth) {
            for (j in 0 until crunchifyWidth) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1)
                }
            }
        }

        //ImageIO.write(image, "png", new File("/var/qr" + invNum + ".png"));
        return InvNumberQR(invNum, guid, date, image)
    }

    fun loadEquipmentDescription(guid: ByteArray): EquipmentDescr? = mapper.loadEquipmentDescr(guid)

    /**
     * Создание записи в таблице логов
     * @param guid к какому оборудованию относится запись
     * @param action действие
     * @param userId совершившего пользователя
     * @param comment комментарий
     * @param date дата и время совершения действия. Если null, то берется текущее время сервера
     *
     * @return количество вставленных записей
     */
    fun logEquipmentChange(guid: ByteArray, action: Action, userId: Int, comment: String, date: LocalDateTime) =
        mapper.logEquipmentChange(guid, action.id, userId, comment, date)

//    /**
//     * Получить id записи с именем entityName справочника refName. Если запись не найдена, она будет создана.
//     * @param refName имя справочника (например "models"). Является именем таблицы в БД.
//     * @param entityName название записи справочника (например "Фольксваген").
//     * @return id найденной или созданной записи справочника.
//     */
//    open fun getIdOrCreateRefByName(refName: String, entityName: String): Int {
//        val id = mapper.getRefIdByName(refName, entityName)
//        if(id != null) {
//            return id
//        }
//        val refEntry = BaseReference(name = entityName)
//        mapper.createReference(refName, refEntry)
//        return refEntry.id
//    }

    /**
     * @return тройка значений. Первое - описание созданной или обновлённой записи оборудования.
     * Второе - признак, что во время создания/обновления оборудования были модифицированы справочники.
     * Третье - признак, что была создана новая запись, а не обновлена существующая
     */
    @Transactional(rollbackOn = [Exception::class])
    @Throws(Exception::class)
    fun putEquipmentDescription(
        eqDescription: EquipmentDescr,
        userId: Int,
        failIfExists: Boolean
    ): Triple<EquipmentDescr, Boolean, Boolean> {
        val oldEquipment = mapper.loadEquipmentDescr(eqDescription.guid)

        val created = oldEquipment == null
        if (oldEquipment != null && failIfExists) {
            throw Exception(EQUIPMENT_ALREADY_EXISTS_MESSAGE)
        }

        var typeId = mapper.getRefIdByName(REF_EQUIPMENT_TYPES_TABLE_NAME, eqDescription.type!!)
        var stateId = mapper.getRefIdByName(REF_EQUIPMENT_STATES_TABLE_NAME, eqDescription.state!!)
        var personId = mapper.getRefIdByName(REF_PERSONS_TABLE_NAME, eqDescription.person!!)
        var refsModified = false
        if (typeId == null || typeId <= 0) {
            val refEntry = BaseReference(name = eqDescription.type!!)
            mapper.createReference(REF_EQUIPMENT_TYPES_TABLE_NAME, refEntry)
            typeId = refEntry.id
            eqTypes = null
            refsModified = true
        }
        if (stateId == null || stateId <= 0) {
            val refEntry = BaseReference(name = eqDescription.state!!)
            mapper.createReference(REF_EQUIPMENT_STATES_TABLE_NAME, refEntry)
            stateId = refEntry.id
            eqStates = null
            refsModified = true
        }
        if (personId == null || personId <= 0) {
            val refEntry = BaseReference(name = eqDescription.person!!)
            mapper.createReference(REF_PERSONS_TABLE_NAME, refEntry)
            personId = refEntry.id
            persons = null
            refsModified = true
        }

        eqDescription.personId = personId
        eqDescription.stateId = stateId
        eqDescription.typeId = typeId
        if (created) {
            mapper.saveEquipment(eqDescription)
        } else {
            mapper.updateEquipment(eqDescription)
        }
        val action: Action
        val comment = if (oldEquipment != null) {
            action = Action.EQUIPMENT_MODIFY
            eqDescription.diff(oldEquipment)
        } else {
            action = Action.EQUIPMENT_CREATE
            eqDescription.toString()
        }
        mapper.logEquipmentChange(eqDescription.guid, action.id, userId, comment, LocalDateTime.now())
        return Triple(eqDescription, refsModified, created)
    }


    fun loadUserInfo(login: String): UserInfo {
        val userInfoByLogin = if (login.lowercase().startsWith("cn=")) {
            mapper.loadUserInfoByDn(login)
        } else {
            mapper.loadUserInfoByLogin(login)
        }
        return userInfoByLogin!!
    }

    @Throws(SQLException::class)
    fun searchEquipment(template: EquipmentSearchTemplate): MutableList<EquipmentDescr> {
        return mapper.searchEquipment(template)
    }

    // TODO: done before this comment

    fun getLog(guid: ByteArray) = mapper.getLog(guid)

    fun getCachedUserInfo(rq: HttpServletRequest, sc: SecurityContext): UserInfo {
        return UserInfo().apply {
            id = 1
        }
//        val userLogin = sc.userPrincipal.name
//        val session= rq.session
//        if (userLogin == null) return UserInfo()
//        var result: UserInfo? = session.getAttribute(userLogin + "_userinfo") as UserInfo?
//        if (null == result) {
//            result = loadUserInfo(userLogin)
//            session.setAttribute(userLogin + "_userinfo", result)
//        }
//        return result
    }

//    fun getCachedUserInfo(session: HttpSession, userLogin: String?): UserInfo {
//        if (userLogin == null) return UserInfo()
//        var result: UserInfo? = session.getAttribute(userLogin + "_userinfo") as UserInfo?
//        if (null == result) {
//            result = loadUserInfo(userLogin)
//            session.setAttribute(userLogin + "_userinfo", result)
//        }
//        return result
//    }

    companion object {
        private val patternRange = Pattern.compile("^\\s*(\\d+)\\s*-\\s*(\\d+)\\s*$")
        private val patternMultiply = Pattern.compile("^\\s*([^*]+)\\*\\s*(\\d+)\\s*$")


        private var persons: Map<Int, BaseReference>? = null
        private var eqStates: Map<Int, BaseReference>? = null
        private var eqTypes: Map<Int, BaseReference>? = null
    }
}
