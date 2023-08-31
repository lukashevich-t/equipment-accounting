package by.gto.inventoryandroid4.other

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//private val dfsInput = arrayOf<DateFormat>(
//        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()),
//        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))

private val dfsInput = object : ThreadLocal<Array<SimpleDateFormat>>() {
    override fun initialValue() = arrayOf(
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
}
//private val dfXML = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
private val dfXML = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
}

//private val dfXML = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
private val dfReadable = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue() = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
}

@Synchronized
fun parseInputDate(sDate: String): Date? {
    for (df in dfsInput.get()) {
        try {
            return df.parse(sDate)
        } catch (e: ParseException) {
        }
    }
    return null
}

fun formatXMLDate(date: Date?): String =
        if (date == null)
            ""
        else {
            val sdf = dfXML.get()
            val format = sdf.format(date)
            format
        }

/**
 * Форматирует дату: ДД.ММ.ГГГГ
 */
fun formatReadableDate(date: Date?): String =
        if (date == null)
            ""
        else {
            val sdf = dfReadable.get()
            val format = sdf.format(date)
            format
        }
