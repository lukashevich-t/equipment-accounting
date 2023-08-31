package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JsonLocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    @Throws(IOException::class)
    override fun serialize(date: LocalDateTime, gen: JsonGenerator, provider: SerializerProvider) {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE_TIME)
        gen.writeString(formattedDate)
    }
}