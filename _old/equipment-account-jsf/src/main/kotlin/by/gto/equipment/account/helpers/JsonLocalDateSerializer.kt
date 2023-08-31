package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import java.io.IOException
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JsonLocalDateSerializer : JsonSerializer<LocalDate>() {
    @Throws(IOException::class)
    override fun serialize(date: LocalDate, gen: JsonGenerator, provider: SerializerProvider) {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        gen.writeString(formattedDate)
    }
}