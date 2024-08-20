package by.gto.equipment.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.sql.Time;

public class JsonSqlTimeDeserializer extends JsonDeserializer<Time> {
    public static final JsonSqlTimeDeserializer INSTANCE = new JsonSqlTimeDeserializer();

    @Override
    public Time deserialize(JsonParser p, DeserializationContext c) throws IOException {
        String sTime = p.getText();
        if(sTime == null) {
            return null;
        }
        return Time.valueOf(sTime);
    }
}
