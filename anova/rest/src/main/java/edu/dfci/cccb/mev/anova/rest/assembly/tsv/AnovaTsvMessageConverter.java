package edu.dfci.cccb.mev.anova.rest.assembly.tsv;

import java.io.IOException;
import java.io.PrintStream;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import edu.dfci.cccb.mev.anova.domain.contract.Anova;
import edu.dfci.cccb.mev.anova.domain.contract.Anova.Entry;
import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.prototype.AbstractTsvHttpMessageConverter;

public class AnovaTsvMessageConverter extends AbstractTsvHttpMessageConverter<Anova>{

  @Override
  protected Anova readInternal (Class<? extends Anova> arg0, HttpInputMessage arg1) throws IOException,
                                                                                   HttpMessageNotReadableException {
    throw new UnsupportedOperationException ("nyi");

  }

  @Override
  protected boolean supports (Class<?> clazz) {
    return Anova.class.isAssignableFrom (clazz);
  }


  @Override
  protected void writeInternal (Anova anova, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    try (PrintStream out = new PrintStream (outputMessage.getBody ())) {
      out.println ("Gene\tp-value");
      for (Entry e : anova.fullResults ())
        out.println (e.geneId () + "\t" + e.pValue ());
    }    
  }

}
