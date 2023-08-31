package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import java.time.format.DateTimeParseException
import java.time.LocalDate
import java.io.IOException
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.format.DateTimeFormatter

class JsonLocalDateDeserializer : JsonDeserializer<LocalDate>() {
    @Throws(IOException::class)
    override fun deserialize(jsonparser: JsonParser,
                             deserializationcontext: DeserializationContext): LocalDate {
        val date = jsonparser.text
        try {
            return LocalDate.parse(date.substring(0, 10), DateTimeFormatter.ISO_DATE)
        } catch (e: DateTimeParseException) {
            throw RuntimeException(e)
        }
    }
}