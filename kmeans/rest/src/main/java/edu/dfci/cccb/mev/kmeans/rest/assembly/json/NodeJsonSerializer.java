package edu.dfci.cccb.mev.kmeans.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans.Node;

public class NodeJsonSerializer extends JsonSerializer<Node>{

  /* (non-Javadoc)
   * @see com.fasterxml.jackson.databind.JsonSerializer#handledType() */
  @Override
  public Class<Node> handledType () {
    return Node.class;
  }

  
  /* (non-Javadoc)
   * @see
   * com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator,
   * com.fasterxml.jackson.databind.SerializerProvider) */
  @Override
  public void serialize (Node value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                                                                                      JsonProcessingException {
    jgen.writeStartObject ();
    jgen.writeStringField ("name", value.id ());
    jgen.writeStringField ("type", value.type ());
    jgen.writeNumberField ("group", value.cluster ());
    jgen.writeNumberField ("x", value.x ());
    jgen.writeNumberField ("y", value.y ());
    jgen.writeEndObject ();
  }

}
