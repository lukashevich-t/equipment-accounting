package by.gto.equipment.account.model

import java.time.LocalDateTime

/**
 * Класс для хранения описания изменении состояния оборудования
 */
data class LogEntryPresentation(
        val guid: String, val actionName: String, val userName: String
        , val time: LocalDateTime
        , val comment: String?)

data class LogEntries(var entries: List<LogEntryPresentation> = emptyList())

/**
 * Класс для записи информации об изменении состояния оборудования в БД
 */
data class LogEntry(
        val guid: String, val actionId: Int, var userId: Int, var time: LocalDateTime, val comment: String?
) {
    constructor(): this("", 0, 0, LocalDateTime.MIN, null)
}