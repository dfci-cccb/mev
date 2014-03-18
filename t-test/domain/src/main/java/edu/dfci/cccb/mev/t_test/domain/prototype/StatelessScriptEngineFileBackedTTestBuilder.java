package edu.dfci.cccb.mev.t_test.domain.prototype;

import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;
import edu.dfci.cccb.mev.t_test.domain.contract.TTestBuilder;

public class StatelessScriptEngineFileBackedTTestBuilder extends AbstractTTestBuilder {

  
  public static final String DATASET_FILENAME = "dataset.tsv";
  public static final String CONFIGURATION_FILENAME = "config.tsv";

  public static final String FULL_FILENAME = "output.tsv";
  public static final String RNK_FILENAME = "rnk.out";
  
  @Override
  public TTest build () throws DatasetException {
    return null;
  }

}
