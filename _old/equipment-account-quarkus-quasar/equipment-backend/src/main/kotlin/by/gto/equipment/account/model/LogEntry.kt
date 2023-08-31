package by.gto.equipment.account.model

import by.gto.equipment.account.helpers.JsonGuidDeserializer
import by.gto.equipment.account.helpers.JsonGuidSerializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDateTime

/**
 * Класс для хранения описания изменении состояния оборудования
 */
public open class LogEntryPresentation {
    @JsonSerialize(using = JsonGuidSerializer::class)
    @JsonDeserialize(using = JsonGuidDeserializer::class)
    var guid: ByteArray = DEFAULT_GUID
    var actionName: String? = null
    var userName: String = ""
    var time: LocalDateTime = LocalDateTime.MIN
    var comment: String? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogEntryPresentation

        if (!guid.contentEquals(other.guid)) return false
        if (actionName != other.actionName) return false
        if (userName != other.userName) return false
        if (time != other.time) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guid.contentHashCode()
        result = 31 * result + (actionName?.hashCode() ?: 0)
        result = 31 * result + userName.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + (comment?.hashCode() ?: 0)
        return result
    }

}

/**
 * Класс для записи информации об изменении состояния оборудования в БД
 */
data class LogEntry(
    @JsonSerialize(using = JsonGuidSerializer::class)
    @JsonDeserialize(using = JsonGuidDeserializer::class)
    val guid: ByteArray,
    val actionId: Int, var userId: Int, var time: LocalDateTime, val comment: String?
) {
    constructor() : this(DEFAULT_GUID, 0, 0, LocalDateTime.MIN, null)
}
