
package gr.mycities.recommendation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import gr.mycities.recommendation.traveller.Traveler;
import java.io.IOException;
import java.io.StringWriter;


// Jackson nedds for serialization
public class TravelerSerializer extends JsonSerializer<Traveler>{

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public void serialize(Traveler t, JsonGenerator jg, SerializerProvider sp) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, t);
        jg.writeFieldName(writer.toString());
    }

}
