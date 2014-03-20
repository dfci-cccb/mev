package edu.dfci.cccb.mev.anova.domain.prototype;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.script.ScriptEngine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.anova.domain.contract.Anova;
import edu.dfci.cccb.mev.anova.domain.contract.AnovaBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.ComposerFactory;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractAnalysisBuilder;

@ToString
@Accessors (fluent = true, chain = true)
public abstract class AbstractAnovaBuilder extends AbstractAnalysisBuilder<AnovaBuilder, Anova> implements AnovaBuilder {

  protected AbstractAnovaBuilder () {
    super ("Anova Analysis");
  }
  
  private @Getter @Setter @Resource (name = "R") ScriptEngine r;
  private @Getter @Setter @Inject ComposerFactory composerFactory;
  
  private @Getter @Setter Selections groupSelections;
  private @Getter @Setter boolean multipleTestCorrectionFlag; //whether to correct for multiple testing or not
  private @Getter @Setter double pValue;
  
  public static final String DATASET_FILENAME = "dataset.tsv";
  public static final String CONFIGURATION_FILENAME = "config.tsv";
  public static final String FULL_FILENAME = "output.tsv";
  public static final String CORRECT_FOR_MULTIPLE_TESTING = "CORRECT_FOR_MULTIPLE_TESTING";
  
  

}
