package pw.react.backend.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JsonDateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        TextNode node = oc.readTree(jp);
        String dateString = node.textValue();
        String[] Dtime=dateString.split("T");
        String[] date= (Dtime[0].split("-"));
        String[] time=Dtime[1].split((":"));
        String[] seconds=time[2].split("\\.");
        return LocalDateTime.of(
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1]),
                Integer.parseInt(seconds[0])
        );

    }
}
