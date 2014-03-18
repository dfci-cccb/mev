package edu.dfci.cccb.mev.t_test.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;

public interface TTest extends Analysis {

  public interface Entry {
    String geneId ();
    double pValue ();
  }
  Iterable<Entry> fullResults ();
}
