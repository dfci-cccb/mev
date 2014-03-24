package edu.dfci.cccb.mev.kmeans.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;

public interface Kmeans extends Analysis {
  public interface Node{
    String id();
    String type();
    int cluster();
    double x();
    double y();
  }

  Iterable<Node> getAllNodes();
}
