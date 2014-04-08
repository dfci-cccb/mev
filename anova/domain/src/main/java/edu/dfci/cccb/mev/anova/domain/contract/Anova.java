package edu.dfci.cccb.mev.anova.domain.contract;

import java.util.Map;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;

public interface Anova extends Analysis {
  public interface Entry {
    String geneId ();
    double pValue ();
    Map<String, Double> logFoldChanges();
  }
  Iterable<Entry> fullResults ();
  String[] logFoldChangePairings();
}
