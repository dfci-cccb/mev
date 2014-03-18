package edu.dfci.cccb.mev.anova.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;

public interface AnovaBuilder extends AnalysisBuilder<AnovaBuilder, Anova> {
  
  AnovaBuilder groupSelections(Selections theSelections); 
  AnovaBuilder pValue(double p);
  AnovaBuilder multipleTestCorrectionFlag(boolean b);
}
