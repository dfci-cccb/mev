package edu.dfci.cccb.mev.anova.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.anova.domain.contract.Anova;
import edu.dfci.cccb.mev.anova.domain.contract.Anova.Entry;
import edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.AbstractAnalysisJsonSerializer;

public class AnovaJsonSerializer  extends AbstractAnalysisJsonSerializer<Anova>{
  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer#handledType() */
  @Override
  public Class<Anova> handledType () {
    return Anova.class;
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer
   * #serializeAnalysisContent(edu.dfci.cccb.mev.dataset
   * .domain.contract.Analysis, com.fasterxml.jackson.core.JsonGenerator,
   * com.fasterxml.jackson.databind.SerializerProvider) */
  @Override
  protected void serializeAnalysisContent (Anova value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                                                                                                              JsonProcessingException {
    super.serializeAnalysisContent (value, jgen, provider);
    jgen.writeArrayFieldStart ("results");
    for (Entry e : value.fullResults ())
      provider.defaultSerializeValue (e, jgen);
    jgen.writeEndArray ();
  }
  
}

