package edu.dfci.cccb.mev.kmeans.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.AbstractAnalysisJsonSerializer;
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans;
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans.Node;

public class KmeansJsonSerializer extends AbstractAnalysisJsonSerializer<Kmeans>{

  @Override
  public Class<Kmeans> handledType () {
    return Kmeans.class;
  }

  
  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer
   * #serializeAnalysisContent(edu.dfci.cccb.mev.dataset
   * .domain.contract.Analysis, com.fasterxml.jackson.core.JsonGenerator,
   * com.fasterxml.jackson.databind.SerializerProvider) */
  @Override
  protected void serializeAnalysisContent (Kmeans analysis, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                                                                                                              JsonProcessingException {
    super.serializeAnalysisContent (analysis, jgen, provider);
    
    jgen.writeArrayFieldStart ("results");
    for (Node n : analysis.getAllNodes ()){
      provider.defaultSerializeValue (n, jgen);
    }
    jgen.writeEndArray (); 
  }
  
}
