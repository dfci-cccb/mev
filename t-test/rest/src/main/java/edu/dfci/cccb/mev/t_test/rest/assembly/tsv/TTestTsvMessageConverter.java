package edu.dfci.cccb.mev.t_test.rest.assembly.tsv;

import java.io.IOException;
import java.io.PrintStream;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.prototype.AbstractTsvHttpMessageConverter;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;

public class TTestTsvMessageConverter extends AbstractTsvHttpMessageConverter<TTest>{

  @Override
  protected TTest readInternal (Class<? extends TTest> arg0, HttpInputMessage arg1) throws IOException,
                                                                                   HttpMessageNotReadableException {
    throw new UnsupportedOperationException ("nyi");

  }

  @Override
  protected boolean supports (Class<?> clazz) {
    return TTest.class.isAssignableFrom (clazz);
  }


  @Override
  protected void writeInternal (TTest tTest, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    try (PrintStream out = new PrintStream (outputMessage.getBody ())) {
      out.println ("Gene\tp-value");
      for (edu.dfci.cccb.mev.t_test.domain.contract.TTest.Entry e : tTest.fullResults ())
        out.println (e.geneId () + "\t" + e.pValue ());
    }    
  }

}
