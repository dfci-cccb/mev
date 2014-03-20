package edu.dfci.cccb.mev.t_test.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.AbstractAnalysisJsonSerializer;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;

public class TTestJsonSerializer  extends AbstractAnalysisJsonSerializer<TTest>{
  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer#handledType() */
  @Override
  public Class<TTest> handledType () {
    return TTest.class;
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer
   * #serializeAnalysisContent(edu.dfci.cccb.mev.dataset
   * .domain.contract.Analysis, com.fasterxml.jackson.core.JsonGenerator,
   * com.fasterxml.jackson.databind.SerializerProvider) */
  @Override
  protected void serializeAnalysisContent (TTest value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                                                                                                              JsonProcessingException {
    super.serializeAnalysisContent (value, jgen, provider);
    jgen.writeArrayFieldStart ("results");
    for (edu.dfci.cccb.mev.t_test.domain.contract.TTest.Entry e : value.fullResults ())
      provider.defaultSerializeValue (e, jgen);
    jgen.writeEndArray ();
  }
  
}
