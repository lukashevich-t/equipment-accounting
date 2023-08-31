package by.gto.equipment.account.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Для отдачи сокращенной информации о пользователе клиентам.
 */
class UserInfo : Serializable {
    var id: Int = 0
    var name: String = ""
    var login: String = ""
    var loginCert: String? = null
    @get:JsonProperty("v")
    var isValid: Boolean = false
    @get:JsonProperty("r")
    var roles: MutableList<String> = ArrayList()

    companion object {
        private const val serialVersionUID = -6288342977008835878L
    }
}