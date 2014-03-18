package edu.dfci.cccb.mev.t_test.domain.prototype;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.script.ScriptEngine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.dataset.domain.contract.ComposerFactory;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractAnalysisBuilder;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;
import edu.dfci.cccb.mev.t_test.domain.contract.TTestBuilder;

@ToString
@Accessors (fluent = true, chain = true)
public abstract class AbstractTTestBuilder extends AbstractAnalysisBuilder<TTestBuilder, TTest> implements TTestBuilder {

  private @Getter @Setter Selection controlSelection;
  private @Getter @Setter Selection experimentSelection;
  private @Getter @Setter String tTestType;
  private @Getter @Setter @Resource (name = "R") ScriptEngine r;
  private @Getter @Setter @Inject ComposerFactory composerFactory;

  
  private @Getter @Setter String testType;  //the type of test-- one sample, two sample, paired
  private @Getter @Setter boolean equalVarianceFlag; //whether we consider the group variances equal in the two-sample test (whether to use Welch's t-test or not)
  private @Getter @Setter double oneSampleMean; //for the one-sample t-test: the user-specified mean
  private @Getter @Setter boolean multipleTestCorrectionFlag; //whether to correct for multiple testing or not
  private @Getter @Setter double pValue; //the user-specified p-value 
  
  protected AbstractTTestBuilder () {
    super ("t-Test Analysis");
  }
  
  public static final String DATASET_FILENAME = "dataset.tsv";
  public static final String CONFIGURATION_FILENAME = "config.tsv";

  public static final String FULL_FILENAME = "output.tsv";
  
    
//flags for the types of test available
public static final String ONE_SAMPLE_T_TEST="one_sample";
public static final String TWO_SAMPLE_T_TEST="two_sample";
public static final String PAIRED_T_TEST="paired";

//some other constants needed for the t-test
public static final String CORRECT_FOR_MULTIPLE_TESTING="CORRECT_FOR_MULTIPLE_TESTING";
public static final String USER_MU="USER_MU";
public static final String EQUAL_VARIANCE="EQUAL_VARIANCE";


}
