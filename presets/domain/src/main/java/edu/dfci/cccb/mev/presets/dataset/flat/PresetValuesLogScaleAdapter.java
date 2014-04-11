package edu.dfci.cccb.mev.presets.dataset.flat;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidCoordinateException;
import edu.dfci.cccb.mev.dataset.domain.contract.Value;
import edu.dfci.cccb.mev.dataset.domain.contract.Values;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleValue;

@RequiredArgsConstructor
public class PresetValuesLogScaleAdapter implements Values, Iterable<Value>, AutoCloseable {

  private final Values values;
  
  @Override
  public double get (String row, String column) throws InvalidCoordinateException {
    
    return log(values.get (row, column));
  }

  @Override
  public void close () throws Exception {
        if(values instanceof AutoCloseable)
          ((AutoCloseable)values).close ();
  }

  private double log(double val){
    if(val==Double.NaN || val<0)
      return Double.NaN;
    else
      return Math.log (val);
  }
  @Override
  public Iterator<Value> iterator () {
    if(values instanceof Iterable){
      @SuppressWarnings("unchecked") final Iterator<Value> it = ((Iterable<Value>)values).iterator (); 
      
      return new Iterator<Value>() {

        @Override
        public boolean hasNext () {
          return it.hasNext ();
        }

        @Override
        public Value next () {
          Value value = it.next();          
          return new SimpleValue(value.row (), value.column (), log(value.value ()));
        }

        @Override
        public void remove () {
          it.remove ();          
        }
        
      };
    }else{
      throw new UnsupportedOperationException ("PresetValues does not support Iterable; values: "+values);
    }
  }

}
