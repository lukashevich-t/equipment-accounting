package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class JsonGuidDeserializer : JsonDeserializer<ByteArray?>() {
    @Throws(IOException::class)
    override fun deserialize(
        jsonparser: JsonParser,
        deserializationcontext: DeserializationContext
    ): ByteArray? {
        val text = jsonparser.text ?: return null
        return try {
            text.toGuidBytes()
        } catch (ignored: Exception) {
            null
        }
    }
}