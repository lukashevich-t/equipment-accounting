package by.gto.inventoryandroid4.model

import by.gto.inventoryandroid4.other.DEFAULT_LOCALE
import java.text.SimpleDateFormat
import java.util.*

data class LogEntry(
        var guid: String, var actionName: String, var userName: String
        , var time: Date?
        , var comment: String?) {
    //    override fun toString() = SimpleDateFormat("dd.MM.yyyy").format(this.time) + " " + this.actionName
    override fun toString(): String {
        val s = SimpleDateFormat("dd.MM.yyyy hh:mm:ss", DEFAULT_LOCALE)
        return "${s.format(time)} -  $actionName"
    }
}
