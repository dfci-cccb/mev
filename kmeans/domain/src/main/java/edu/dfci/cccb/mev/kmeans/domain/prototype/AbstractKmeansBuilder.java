package edu.dfci.cccb.mev.kmeans.domain.prototype;

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
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans;
import edu.dfci.cccb.mev.kmeans.domain.contract.KmeansBuilder;

@ToString
@Accessors (fluent = true, chain = true)
public abstract class AbstractKmeansBuilder extends AbstractAnalysisBuilder<KmeansBuilder, Kmeans> implements KmeansBuilder {


  protected AbstractKmeansBuilder () {
    super ("K-means clustering");
  }
  
  private @Getter @Setter int clusterCount;
  private @Getter @Setter Selection sampleSelection;
  private @Getter @Setter @Resource (name = "R") ScriptEngine r;
  private @Getter @Setter @Inject ComposerFactory composerFactory;
  
  public static final String DATASET_FILENAME = "dataset.tsv";
  public static final String FULL_FILENAME = "output.tsv";
  public static final String CONFIGURATION_FILENAME = "config.tsv";
  public static final String INFILE="INFILE";
  public static final String OUTFILE="OUTFILE";
  public static final String SAMPLE_FILE="SAMPLE_FILE";
  public static final String CLUSTER_COUNT="CLUSTER_COUNT";

}
