package edu.dfci.cccb.mev.dataset.domain.zip;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by antony on 8/8/16.
 */
public class AnalysisJsonSerializer extends JsonSerializer<AnalysisJson> {

    @Override
    public void serialize(AnalysisJson analysisJson, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeRaw(analysisJson.toJson());
    }
}
