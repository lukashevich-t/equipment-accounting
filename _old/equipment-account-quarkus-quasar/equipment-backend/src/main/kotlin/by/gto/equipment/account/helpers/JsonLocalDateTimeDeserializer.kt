package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import java.time.format.DateTimeParseException
import java.io.IOException
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JsonLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    @Throws(IOException::class)
    override fun deserialize(jsonparser: JsonParser,
                             deserializationcontext: DeserializationContext): LocalDateTime {
        val date = jsonparser.text
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: DateTimeParseException) {
            throw RuntimeException(e)
        }
    }
}