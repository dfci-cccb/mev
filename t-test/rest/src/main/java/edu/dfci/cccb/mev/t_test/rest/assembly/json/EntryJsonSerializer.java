package edu.dfci.cccb.mev.t_test.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.t_test.domain.contract.TTest.Entry;


public class EntryJsonSerializer extends JsonSerializer<Entry>{

  
  /* (non-Javadoc)
   * @see com.fasterxml.jackson.databind.JsonSerializer#handledType() */
  @Override
  public Class<Entry> handledType () {
    return Entry.class;
  }
  
  @Override
  public void serialize (Entry value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
  JsonProcessingException {
    jgen.writeStartObject ();
    jgen.writeStringField ("id", value.geneId ());
    jgen.writeNumberField ("pValue", value.pValue ());
    jgen.writeEndObject ();
}

}
