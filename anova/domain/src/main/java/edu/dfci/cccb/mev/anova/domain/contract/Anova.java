package edu.dfci.cccb.mev.anova.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;

public interface Anova extends Analysis {
  public interface Entry {
    String geneId ();
    double pValue ();
  }
  Iterable<Entry> fullResults ();
}
