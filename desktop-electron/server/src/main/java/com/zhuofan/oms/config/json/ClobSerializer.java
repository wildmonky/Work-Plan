package com.zhuofan.oms.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

public class ClobSerializer extends JsonSerializer<Clob> {

    @Override
    public Class<Clob> handledType() {
        return Clob.class;
    }

    @Override
    public void serialize(Clob value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try (Reader reader = value.getCharacterStream()) {
            String clobString = IOUtils.toString(reader);
            gen.writeString(clobString);
        } catch (SQLException e) {
            gen.writeString("CLOB转字符串失败");
        }
    }
}
