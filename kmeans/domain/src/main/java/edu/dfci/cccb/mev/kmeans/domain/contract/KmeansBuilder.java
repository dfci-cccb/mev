package edu.dfci.cccb.mev.kmeans.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;

public interface KmeansBuilder extends AnalysisBuilder<KmeansBuilder, Kmeans> {
  KmeansBuilder sampleSelection(Selection s);
  KmeansBuilder clusterCount(int k);

}
