package edu.dfci.cccb.mev.kmeans.rest.assembly.tsv;

import java.io.IOException;
import java.io.PrintStream;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.prototype.AbstractTsvHttpMessageConverter;
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans;
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans.Node;

public class KmeansTsvMessageConverter extends AbstractTsvHttpMessageConverter<Kmeans>{

  /* (non-Javadoc)
   * @see
   * org.springframework.http.converter.AbstractHttpMessageConverter#supports
   * (java.lang.Class) */
  @Override
  protected boolean supports (Class<?> clazz) {
    return Kmeans.class.isAssignableFrom (clazz);
  }

  @Override
  protected Kmeans readInternal (Class<? extends Kmeans> clazz, HttpInputMessage inputMessage) throws IOException,HttpMessageNotReadableException {
    throw new UnsupportedOperationException ("nyi");
  }

  @Override
  protected void writeInternal (Kmeans analysis, HttpOutputMessage outputMessage) throws IOException,
                                                                          HttpMessageNotWritableException {
try (PrintStream out = new PrintStream (outputMessage.getBody ())) {
out.println ("ID\tNode Type\tCluster Membership\tx\ty");
for (Node e : analysis.getAllNodes ())
out.println (e.id () + "\t" +
e.type () + "\t" +
e.cluster ()+ "\t" +
e.x () + "\t" +
e.y ());
}    
  }

}
