package by.gto.equipment.account.helpers


import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

//private val dfsOther = arrayOf(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault(Locale.Category.FORMAT)))

fun date2LocalDate(value: Date?): LocalDate? {
    return if (value == null) {
        null
    } else Instant.ofEpochMilli(value.time).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Date?.toLocalDate(): LocalDate? =
        if (this == null) {
            null
        } else {
            Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()
        }

fun Date?.toLocalDateTime(): LocalDateTime? =
        if (this == null) {
            null
        } else {
            Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

// to delete!:
//fun localDate2Date(value: LocalDate?): Date? {
//    return if (value == null) {
//        null
//    } else {
//        Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant())
//    }
//}

fun LocalDate?.toDate(): Date? = if (this == null) {
        null
    } else {
        Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

fun LocalDateTime?.toDate(): Date? = if (this == null) {
        null
    } else {
        Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
    }

// to delete!:
//fun parseAllDate(sDate: String?): LocalDate? {
//    if (StringUtils.isBlank(sDate)) {
//        return null
//    }
//
//    val dfsOther1 = arrayOf(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault(Locale.Category.FORMAT)))
//    for (df in dfsOther1) {
//        try {
//            return df.parse(sDate).toLocalDate()
//        } catch (e: ParseException) {
//        }
//
//    }
//    return null
//}
//
// to delete!:
//fun parseLocalDate(sDate: String): LocalDate? {
//    if (StringUtils.isBlank(sDate)) {
//        return null
//    }
//    try {
//        return LocalDate.parse(StringUtils.substring(sDate, 0, 10))
//    } catch (ignored: DateTimeParseException) {
//        return null
//    }
//
//}
