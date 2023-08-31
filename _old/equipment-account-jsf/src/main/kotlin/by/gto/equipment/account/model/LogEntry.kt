package by.gto.equipment.account.model

import java.time.LocalDateTime

data class LogEntry(
        val guid: String, val actionName: String, val userName: String
        , val time: LocalDateTime
        , val comment: String?)

data class LogEntries( var entries: List<LogEntry> = emptyList())
