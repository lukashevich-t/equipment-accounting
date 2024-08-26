package by.gto.equipment.config.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class ConfigBeans {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        SimpleModule m = new SimpleModule("MyModule", new Version(1, 0, 0, null, "", ""));
        m.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        m.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        m.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        m.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);

        m.addSerializer(java.sql.Time.class, JsonSqlTimeSerializer.INSTANCE);
        m.addDeserializer(java.sql.Time.class, JsonSqlTimeDeserializer.INSTANCE);

        ObjectMapper om = JsonMapper.builder()
//                .enable(MapperFeature.AUTO_DETECT_CREATORS,
//                        MapperFeature.AUTO_DETECT_FIELDS,
//                        MapperFeature.AUTO_DETECT_GETTERS,
//                        MapperFeature.AUTO_DETECT_IS_GETTERS)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .addModule(m)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
                .build();
        return om;
    }
}
