package by.gto.equipment.account.dao

import by.gto.equipment.account.e.RecordNotFoundException
import by.gto.equipment.account.helpers.toDate
import by.gto.equipment.account.helpers.toLocalDate
import by.gto.equipment.account.helpers.toLocalDateTime
import by.gto.equipment.account.model.*
import by.gto.library.helpers.GuidHelpers
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.ResultSetHandler
import org.apache.commons.lang.StringUtils
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.enterprise.context.ApplicationScoped
import javax.sql.DataSource

@ApplicationScoped
open class DaoJdbcJdbcUtils {
    @Resource(mappedName = DS_JNDI_NAME)
    private val ds: DataSource? = null

//    constructor() {
//        println("DaoJdbcJdbcUtils constructor")
//    }
//
//    @PostConstruct
//    open fun postConstruct() {
//        println("DaoJdbcJdbcUtils postConstruct")
//    }

    @Throws(SQLException::class)
    open fun getEquipmentStates(): Map<Int, BaseReference> {
        val run = QueryRunner(ds)
        val r = run.query<Map<Int, BaseReference>>("SELECT es.id, es.name FROM equipment_state es order by es.name") { rs ->
            val res = LinkedHashMap<Int, BaseReference>()
            while (rs.next()) {
                val id = rs.getInt(1)
                res.put(id, BaseReference(id, rs.getString(2)))
            }
            res
        }
        return r
    }

    @Throws(SQLException::class)
    open fun getEquipmentTypes(): Map<Int, BaseReference> {
        val run = QueryRunner(ds)
        val r = run.query<Map<Int, BaseReference>>("SELECT et.id, et.name FROM equipment_type et order by et.name") { rs ->
            val res = LinkedHashMap<Int, BaseReference>()
            while (rs.next()) {
                val id = rs.getInt(1)
                res.put(id, BaseReference(id, rs.getString(2)))
            }
            res
        }
        return r

    }

    @Throws(SQLException::class)
    open fun getResponsiblePersons(): Map<Int, BaseReference> {
        val run = QueryRunner(ds)
        val r = run.query<Map<Int, BaseReference>>("SELECT rp.id, rp.name FROM responsible_person rp order by rp.name") { rs ->
            val res = LinkedHashMap<Int, BaseReference>()
            while (rs.next()) {
                val id = rs.getInt(1)
                res.put(id, BaseReference(id, rs.getString(2)))
            }
            res
        }
        return r
    }

//    val models: ReferenceAnswerJSON
//        @Throws(SQLException::class)
//        get() {
//            val run = QueryRunner(ds)
//            val r = run.query<List<BaseReference>>("SELECT t.id, concat(t.name_rus, ' / ', t.name_eng) name FROM gai_model t") { rs ->
//                val res = ArrayList<BaseReference>()
//                while (rs.next()) {
//                    res.add(BaseReference(rs.getInt(1), rs.getString(2)))
//                }
//                res
//            }
//            return ReferenceAnswerJSON(Date(), 0, r)
//        }

//    fun getEquipment(guid: UUID): Equipment? {
//        val run = QueryRunner(ds)
//        try {
//            return run.query("SELECT e.type_id, e.person_id, e.state_id, e.comment, e.inv_number, e.purchase_date, e.serial FROM equipment e WHERE  e.id = ? LIMIT 1",
//                    ResultSetHandler<Equipment> { rs ->
//                        if (rs.next()) {
//                            Equipment(guid.toString(), rs.getShort("type_id"),
//                                    rs.getInt("person_id"), rs.getInt("state_id"),
//                                    rs.getString("comment"), rs.getString("inv_number"),
//                                    rs.getDate("purchase_date"),
//                                    rs.getString("serial"))
//                        } else {
//                            null
//                        }
//                    }, GuidHelpers.guidAsBytes(guid))
//        } catch (e: SQLException) {
//            return null
//        }
//    }

    open fun getEquipmentDescr(guid: UUID): EquipmentDescr? {
        val query = "SELECT e.id, e.type_id, e.person_id, e.state_id, e.comment, e.inv_number, e.purchase_date, " +
                "e.serial, es.name AS state, et.name AS eq_type, rp.name AS person FROM equipment e\n" +
                "  LEFT JOIN responsible_person rp ON e.person_id = rp.id\n" +
                "  LEFT JOIN equipment_state es ON e.state_id = es.id\n" +
                "  LEFT JOIN equipment_type et ON e.type_id = et.id \n" +
                "WHERE (e.id=?)"
        val run = QueryRunner(ds)
        try {
            return run.query(query,
                    ResultSetHandler<EquipmentDescr> { rs ->
                        if (rs.next()) {
                            EquipmentDescrFromResultSet(rs)
                        } else {
                            null
                        }
                    }, GuidHelpers.guidAsBytes(guid))
        } catch (e: SQLException) {
            return null
        }
    }

    @Throws(SQLException::class)
    open fun searchEquipment(template: Equipment): MutableList<EquipmentDescr> {
        val run = QueryRunner(ds)
        val query = "SELECT e.id, e.type_id, e.person_id, e.state_id, e.comment, e.inv_number, e.purchase_date, " +
                "e.serial, es.name AS state, et.name AS eq_type, rp.name AS person FROM equipment e\n" +
                "  LEFT JOIN responsible_person rp ON e.person_id = rp.id\n" +
                "  LEFT JOIN equipment_state es ON e.state_id = es.id\n" +
                "  LEFT JOIN equipment_type et ON e.type_id = et.id \n" +
                "where "
        var cond = ""
        val params = ArrayList<Any>()
        try {
            val guid = UUID.fromString(template.guid)
            cond = "$cond (e.id=?) and"
            params.add(GuidHelpers.guidAsBytes(guid))
        } catch (ignored: Exception) {
        }

        if (template.personId > 0) {
            cond = "$cond (e.person_id=?) and"
            params.add(template.personId)
        }

        if (template.typeId > 0) {
            cond = "$cond (e.type_id=?) and"
            params.add(template.typeId)
        }

        if (template.stateId > 0) {
            cond = "$cond (e.state_id=?) and"
            params.add(template.stateId)
        }

        if (!StringUtils.isEmpty(template.comment)) {
            cond = "$cond (e.comment like ?) and"
            params.add("%" + template.comment?.trim() + "%")
        }

        val purchaseDate = template.purchaseDate
        if (purchaseDate != null) {
            cond = "$cond (e.purchase_date = ?) and"
            params.add(purchaseDate.toDate()!!)
        }

        if (!StringUtils.isEmpty(template.serial)) {
            cond = "$cond (e.serial like ?) and"
            params.add("%" + template.serial?.trim() + "%")
        }

        if (!StringUtils.isEmpty(template.invNumber)) {
            cond = "$cond (e.inv_number like ?) and"
            params.add("%" + template.invNumber.trim() + "%")
        }
        if ("" == cond) {
            return ArrayList<EquipmentDescr>()
        }
        cond = cond.substring(0, cond.length - 4)
        return run.query<MutableList<EquipmentDescr>>("$query $cond", params.toTypedArray()) { rs ->
            val res = ArrayList<EquipmentDescr>()
            while (rs.next()) {
                res.add(EquipmentDescrFromResultSet(rs))
            }
            res
        }
    }

    @Throws(SQLException::class)
    private fun EquipmentDescrFromResultSet(rs: ResultSet): EquipmentDescr {
        val r = EquipmentDescr()
        r.guid = GuidHelpers.guidFromBytes(rs.getBytes("id")).toString()
        r.comment = rs.getString("comment")
        r.invNumber = rs.getString("inv_number")

        r.purchaseDate = (rs.getDate("purchase_date") as java.util.Date?).toLocalDate()
        r.state = rs.getString("state")
        r.type = rs.getString("eq_type")
        r.person = rs.getString("person")
        r.typeId = rs.getInt("type_id")
        r.stateId = rs.getInt("state_id")
        r.personId = rs.getInt("person_id")
        r.serial = rs.getString("serial")
        return r
    }

    @Throws(SQLException::class)
    open fun getIdByName(tableName: String, name: String): Int? {
        val run = QueryRunner(ds)
        val id = run.query<Int?>(String.format(qGetIdOfName, tableName), name, { rs ->
            if (rs.next()) rs.getInt(1) else null
        })
        return id
    }

    @Throws(Exception::class)
    open fun putEquipment(eq: Equipment): EquipmentDescr {
        val query = """
INSERT INTO equipment(  id,   type_id,  person_id,  state_id,  comment,  inv_number,  purchase_date, serial)
  VALUES (?,   ?,  ?,  ?,  ?,  ?, ?, ?)
ON DUPLICATE KEY UPDATE
type_id = VALUES(equipment.type_id),
person_id = VALUES(equipment.person_id),
state_id = VALUES(equipment.state_id),
comment = VALUES(equipment.comment),
serial = VALUES(equipment.serial)"""
        val guid = UUID.fromString(eq.guid)
        val params = arrayOfNulls<Any>(8)
        params[0] = GuidHelpers.guidAsBytes(guid)
        params[1] = eq.typeId
        params[2] = eq.personId
        params[3] = eq.stateId
        params[4] = eq.comment
        params[5] = eq.invNumber
        params[6] = eq.purchaseDate.toDate()
        params[7] = eq.serial

        val run = QueryRunner(ds)
        val cnt = run.update(query, *params)
        if (cnt == 0) {
            throw Exception("Ошибка вставки/обновления")
        }

        val t = Equipment()
        t.guid = eq.guid
        return searchEquipment(t)[0]
    }


    @Throws(SQLException::class)
    open fun getEquipmentsByGuidList(sGuids: List<String?>?): List<Equipment> {
        if (sGuids == null) {
            return ArrayList<Equipment>()
        }
        val run = QueryRunner(ds)

        val query = "SELECT e.id, e.type_id, e.person_id, e.state_id, e.comment, e.inv_number, e.purchase_date, e.serial FROM equipment e WHERE "
        val cond = StringBuilder()
        val bGuids = ArrayList<ByteArray>()
        for (i in sGuids.indices) {
            try {
                bGuids.add(GuidHelpers.guidAsBytes(UUID.fromString(sGuids[i])))
                cond.append(" e.id = ? or")
            } catch (ignored: Exception) {
            }

        }
        if (bGuids.size == 0) {
            return ArrayList()
        }

        cond.setLength(cond.length - 3)
        val result = run.query<List<Equipment>>(query + cond.toString(), ResultSetHandler<List<Equipment>> { rs ->
            val res = ArrayList<Equipment>()
            while (rs.next()) {
                res.add(Equipment(GuidHelpers.guidFromBytes(rs.getBytes("id")).toString(),
                        rs.getInt("type_id"),
                        rs.getInt("person_id"),
                        rs.getInt("state_id"),
                        rs.getString("comment"),
                        rs.getString("inv_number"),
                        (rs.getDate("purchase_date") as java.util.Date?).toLocalDate(),
                        rs.getString("serial")
                ))
            }
            res
        }, *bGuids.toTypedArray())
        return result
    }

    open fun putBlob(blobSize: Int, binSize: Int) {
        val run = QueryRunner(ds)
        val query = "INSERT INTO test (blb32, varbin32) VALUES (?,?)"

        try {
            run.update(query, ByteArray(blobSize), ByteArray(binSize))
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    @Throws(SQLException::class)
    open fun createEquipmentType(type: String): Int {
        return createReference("equipment_type", type)
    }

    @Throws(SQLException::class)
    open fun createEquipmentState(state: String): Int {
        return createReference("equipment_state", state)
    }

    @Throws(SQLException::class)
    open fun createPerson(personName: String): Int {
        return createReference("responsible_person", personName)
    }

    @Throws(SQLException::class)
    private fun createReference(tableName: String, value: String): Int {
        val run = QueryRunner(ds)
        val id = run.insert(String.format(qCreateReference, tableName), ResultSetHandler { rs -> if (rs.next()) rs.getInt(1) else 0 }, value)
        return id!!
    }

    open fun getUserInfoByLogin(userLogin: String, certLogin: Boolean): UserInfo {
        val query = """SELECT
            u.id,
            u.name,
            u.login,
            u.login_cert,
            u.valid,
            u.region_id,
            r.name role_name
        FROM user u
            LEFT JOIN user_role ur ON ur.user_id = u.id
            LEFT JOIN role r ON ur.role_id = r.id
        WHERE u.${if (certLogin) "login_cert" else "login"} = ?"""
        val r = UserInfo()
        val run = QueryRunner(ds)

        run.query(query,
                ResultSetHandler<UserInfo> { rs ->
                    while (rs.next()) {
                        r.id = rs.getInt("id")
                        r.name = rs.getString("name")
                        r.login = rs.getString("login")
                        r.loginCert = rs.getString("login_cert")
                        r.isValid = rs.getBoolean("valid")
                        r.region = rs.getInt("region_id")
                        r.roles.add(rs.getString("role_name"))
                    }
                    r
                }, userLogin)
        if (r.id == 0) {
            throw RecordNotFoundException("Не найден пользователь ${userLogin}")
        }
        return r
    }

    open fun clearTestTable(): Int {
        val run = QueryRunner(ds)
        return run.update("delete from testtable")
    }

    open fun appendTestTable() {
        val run = QueryRunner(ds)
        run.update("insert into testtable (value) values(?)", System.currentTimeMillis().toString())
    }

    /**
     * Создание записи в таблице логов
     * @param eq к какому оборудованию относится запись
     * @param action действие
     * @param id совершившего пользователя
     * @param comment комментарий
     * @param date дата и время совершения действия. Если null, то берется текущее время сервера
     *
     * @return количество вставленных записей
     */
    open fun logEquipmentChange(eq: EquipmentDescr, action: Action, userId: Int, comment: String?, date: LocalDateTime? = null): Int {
        val d = if (date == null) Date() else date.toDate()
        return QueryRunner(ds)
                .update("INSERT INTO inventoryaccount.log (equipment_id, action_id, user_id, comment, `time`) VALUES (?, ?, ?, ?, ?)",
                        GuidHelpers.guidAsBytes(eq.guid), action.id, userId, comment, d)
    }

    open fun getLog(guid: String): List<LogEntry> {
        val query = """
SELECT l.equipment_id eqipment_id, a.`name` action_name, u.`name` user_name, l.`time`, l.`comment`
  FROM log l
  left join action a ON (l.action_id = a.id)
  LEFT JOIN user u ON (l.user_id = u.id)
  WHERE l.equipment_id = ?
  order by l.`time` desc"""
        return QueryRunner(ds).query<List<LogEntry>>(query, ResultSetHandler<List<LogEntry>> { rs ->
            val r = ArrayList<LogEntry>()
            while (rs.next()) {
                r.add(LogEntry(GuidHelpers.guidFromBytes(rs.getBytes("eqipment_id")).toString(),
                        rs.getString("action_name") ?: "",
                        rs.getString("user_name") ?: "",
                        rs.getDate("time").toLocalDateTime() ?: LocalDateTime.of(1, 1, 1, 0, 0),
                        rs.getString("comment"))
                )
            }
            r
        }, GuidHelpers.guidAsBytes(guid))
    }


//    @Throws(SQLException::class)
//    open fun testTransactions() {
//        val run = QueryRunner(ds)
//
//        val d = Date()
//        val sql = "INSERT INTO testtable(value, t) VALUES(?, ?)"
//        val update1 = run.update(sql, Math.random().toString(), d)
//        val update2 = run.update(sql, Math.random().toString(), d)
//        throw SQLException("KKK")
//    }

    companion object {

        private val connectionHashes = HashSet<Int>()
        private val threads = HashSet<Thread>()

        private val qGetIdOfName = "SELECT id FROM %s et WHERE LOWER(et.name) = LOWER(?) LIMIT 1"
        private val qCreateReference = "INSERT INTO %s (`name`) VALUES (?)"
    }
}
