package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

@Provider
@Produces(MediaType.APPLICATION_JSON)
open class JacksonConfig @Throws(Exception::class)
constructor() : ContextResolver<ObjectMapper> {
    private val objectMapper = ObjectMapper()

    init {
        val m = SimpleModule("MyModule", Version(1, 0, 0, null, null, null))
        m.addSerializer(LocalDateTime::class.java, JsonLocalDateTimeSerializer())
        m.addDeserializer(LocalDateTime::class.java, JsonLocalDateTimeDeserializer())
        m.addSerializer(LocalDate::class.java, JsonLocalDateSerializer())
        m.addDeserializer(LocalDate::class.java, JsonLocalDateDeserializer())
        this.objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")
        this.objectMapper.registerModule(m)
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }


    override fun getContext(objectType: Class<*>): ObjectMapper {
        return objectMapper
    }
}
