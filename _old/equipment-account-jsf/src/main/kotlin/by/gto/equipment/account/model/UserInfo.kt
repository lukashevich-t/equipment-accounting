package by.gto.equipment.account.model

import by.gto.equipment.account.helpers.JsonLocalDateSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.io.Serializable
import java.time.LocalDate
import java.util.*


/**
 * Для отдачи сокращенной информации о пользователе клиентам
 */
class UserInfo : Serializable {
    var id: Int = 0
    var name: String = ""
    var login: String = ""
    var loginCert: String? = null
    @get:JsonSerialize(using = JsonLocalDateSerializer::class)
    var dateOfStart: LocalDate? = null
    @get:JsonSerialize(using = JsonLocalDateSerializer::class)
    var dateOfEnd: LocalDate? = null
    @get:JsonProperty("v")
    var isValid: Boolean = false
    @get:JsonProperty("r")
    var region: Int = 0
    var roles: MutableList<String> = ArrayList()

    companion object {
        /**
         * Название таблицы в базе данных, которая хранит справочник.
         * Также по этому имени ссылаемся на этот справочник в таблице ref_version
         */
        val TABLE_NAME = "V_User"
        private const val serialVersionUID = -6288342977008835878L
    }
}