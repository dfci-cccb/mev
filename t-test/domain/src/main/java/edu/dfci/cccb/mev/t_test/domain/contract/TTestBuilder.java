package edu.dfci.cccb.mev.t_test.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;

public interface TTestBuilder extends AnalysisBuilder<TTestBuilder, TTest> {
  TTestBuilder controlSelection (Selection selection);
  TTestBuilder experimentSelection (Selection selection);
  TTestBuilder testType(String type);
  TTestBuilder equalVarianceFlag(boolean flag);
  TTestBuilder oneSampleMean(double val);
  TTestBuilder multipleTestCorrectionFlag(boolean flag);
  TTestBuilder pValue(double p);
}
