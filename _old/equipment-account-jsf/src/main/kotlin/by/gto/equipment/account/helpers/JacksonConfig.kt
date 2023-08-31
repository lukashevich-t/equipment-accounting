package by.gto.equipment.account.helpers

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import org.slf4j.LoggerFactory
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
    private val objectMapper: ObjectMapper

    init {
        this.objectMapper = ObjectMapper()
        val m = SimpleModule("MyModule", Version(1, 0, 0, null))
        m.addSerializer(LocalDateTime::class.java, JsonLocalDateTimeSerializer())
        m.addDeserializer(LocalDateTime::class.java, JsonLocalDateTimeDeserializer())
        m.addSerializer(LocalDate::class.java, JsonLocalDateSerializer())
        m.addDeserializer(LocalDate::class.java, JsonLocalDateDeserializer())
        this.objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd")
        this.objectMapper.registerModule(m)
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

//        val log = LoggerFactory.getLogger(JacksonConfig::class.java)
    }


    override fun getContext(objectType: Class<*>): ObjectMapper {
        return objectMapper
    }
}

//open class JsonDateSerializer : JsonSerializer<Date>() {
//
//    @Throws(IOException::class, JsonProcessingException::class)
//    override fun serialize(date: Date, gen: JsonGenerator, provider: SerializerProvider) {
//
//        val formattedDate = dateFormat.format(date)
//
//        gen.writeString(formattedDate)
//
//    }
//
//    companion object {
//
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'")
//    }
//
//}
//
//open class JsonLocalDateSerializer : JsonSerializer<LocalDate>() {
//
//    @Throws(IOException::class, JsonProcessingException::class)
//    override fun serialize(date: LocalDate, gen: JsonGenerator, provider: SerializerProvider) =
//
//            gen.writeString("Here comes date")
//
//
//}
//
//
//@Startup
//@Singleton
//open class StartupClass {
//    @PostConstruct
//    open fun startup() {
//        try {
//            val mapper = ObjectMapper()
//            val testModule = SimpleModule("MyModule", Version(1, 0, 0, null))
//            testModule.addSerializer(LocalDate::class.java, JsonLocalDateSerializer()) // assuming serializer declares correct class to bind to
//            mapper.registerModule(testModule)
//        } catch (ex: Throwable) {
//            println(ex)
//        }
//    }
//}