package by.gto.equipment.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.sql.Time;

public class JsonSqlTimeSerializer extends JsonSerializer<Time> {
    public static final JsonSqlTimeSerializer INSTANCE = new JsonSqlTimeSerializer();

    @Override
    public void serialize(Time time, JsonGenerator gen, SerializerProvider p)
            throws IOException {
        if (time == null) {
            gen.writeNull();
        } else {
            gen.writeString(time.toString());
        }
    }
}