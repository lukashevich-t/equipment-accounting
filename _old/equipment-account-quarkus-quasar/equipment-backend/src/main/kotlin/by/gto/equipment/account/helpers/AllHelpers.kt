package by.gto.equipment.account.helpers

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID

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

fun UUID.toBytes(): ByteArray {
    val longOne = this.mostSignificantBits
    val longTwo = this.leastSignificantBits
    return byteArrayOf(
        (longOne ushr 32).toByte(),  // 0
        (longOne ushr 40).toByte(),  // 1
        (longOne ushr 48).toByte(),  // 2
        (longOne ushr 56).toByte(),  // 3
        (longOne ushr 16).toByte(),  // 4
        (longOne ushr 24).toByte(),  // 5
        longOne.toByte(),  // 6
        (longOne ushr 8).toByte(),  // 7

        (longTwo ushr 56).toByte(),  // 8
        (longTwo ushr 48).toByte(),  // 9
        (longTwo ushr 40).toByte(),  // 10
        (longTwo ushr 32).toByte(),  // 11
        (longTwo ushr 24).toByte(),  // 12
        (longTwo ushr 16).toByte(),  // 13
        (longTwo ushr 8).toByte(),  // 14
        longTwo.toByte() // 15
    )
}

/**
 * Преобразовать строку вида 91df333a-a78d-4c20-81ae-7abb9292941f в массив 16 байт для хранения в БД
 */
@Suppress("NOTHING_TO_INLINE")
inline fun String.toGuidBytes() = UUID.fromString(this).toBytes()

fun ByteArray.toGuidString(): String = this.toGuid().toString()

fun ByteArray.toGuid(): UUID {
    val mostSigBits = (this[0].toLong() shl 32 and 0x000000ff00000000L
            or (this[1].toLong() shl 40 and 0x0000ff0000000000L)
            or (this[2].toLong() shl 48 and 0x00ff000000000000L)
            or (this[3].toLong() shl 56 and -0x100000000000000L)
            or (this[4].toLong() shl 16 and 0x0000000000ff0000L)
            or (this[5].toLong() shl 24 and 0x00000000ff000000L)
            or (this[6].toLong() and 0x00000000000000ffL)
            or (this[7].toLong() shl 8 and 0x000000000000ff00L))
    val leastSigBits = (this[8].toLong() shl 56 and -0x100000000000000L
            or (this[9].toLong() shl 48 and 0x00ff000000000000L)
            or (this[10].toLong() shl 40 and 0x0000ff0000000000L)
            or (this[11].toLong() shl 32 and 0x000000ff00000000L)
            or (this[12].toLong() shl 24 and 0x00000000ff000000L)
            or (this[13].toLong() shl 16 and 0x0000000000ff0000L)
            or (this[14].toLong() shl 8 and 0x000000000000ff00L)
            or (this[15].toLong() and 0x00000000000000ffL))
    return UUID(mostSigBits, leastSigBits)
}