package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException

class JsonGuidSerializer : JsonSerializer<ByteArray?>() {
    @Throws(IOException::class)
    override fun serialize(bytes: ByteArray?, gen: JsonGenerator, provider: SerializerProvider) {
        if (bytes == null) {
            gen.writeNull()
        } else {
            gen.writeString(bytes.toGuidString())
        }
    }
}