package edu.dfci.cccb.mev.anova.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.anova.domain.contract.Anova.Entry;
import edu.dfci.cccb.mev.anova.domain.prototype.AbstractAnovaBuilder;


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
    jgen.writeArrayFieldStart ("pairwise_log_fold_change");
    for(String pairing:value.logFoldChanges ().keySet ()){
      String[] partners=pairing.split (AbstractAnovaBuilder.PAIRING_DELIMITER);
      jgen.writeStartObject ();
      jgen.writeStringField ("partnerA", partners[0]);
      jgen.writeStringField ("partnerB", partners[1]);
      jgen.writeNumberField ("ratio", value.logFoldChanges ().get(pairing));
      jgen.writeEndObject ();
    }
    jgen.writeEndArray ();
    jgen.writeEndObject ();
}

}
