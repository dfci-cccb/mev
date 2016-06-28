package edu.dfci.cccb.mev.edger.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Setter;
import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.dataset.domain.r.AbstractDispatchedRAnalysisBuilder;
import edu.dfci.cccb.mev.dataset.domain.r.annotation.Parameter;
import edu.dfci.cccb.mev.dataset.domain.r.annotation.R;
import edu.dfci.cccb.mev.dataset.domain.r.annotation.Result;

@R ("function(dataset, control=NA, experiment=NA, method='fdr', log = TRUE){\n"
        + "library('edgeR')\n"
    + "ALL_SAMPLES=colnames(dataset)\n"
    + "if (!(experiment %in% ALL_SAMPLES) || !(control %in% ALL_SAMPLES)){\n"
    + "  stop('Could not find samples corresponding to the desired contrast.')\n"
    + "}\n"
    + "CurrMtx=dataset[,c(experiment, control)]\n"
    + "CONDITIONS=array(NA, dim=c(1,length(colnames(CurrMtx))))\n"
    + "CONDITIONS[1:length(experiment)]='Experiment'\n"
    + "CONDITIONS[(length(experiment)+1):length(colnames(CurrMtx))]='Control'\n"

    + "cds<-DGEList(counts=CurrMtx, group=CONDITIONS)\n"

    + "cds<-estimateCommonDisp(cds)\n"

    + "de<-exactTest(cds, dispersion='common', pair=c('Experiment', 'Control'))\n"
    + "res<-topTags(de, n=dim(de$table)[1], adjust.method=method)\n"
    + "return(res$table)\n" +
    "}")
@Accessors (fluent = true, chain = true)
public class EdgeBuilder extends AbstractDispatchedRAnalysisBuilder<EdgeBuilder, Edge> {

  private @Parameter @Setter Set<String> control;
  private @Parameter @Setter Set<String> experiment;
  private @Parameter @Setter String method;

  private Edge.EdgeParams params;
  public EdgeBuilder params(Edge.EdgeParams params){
    name(params.name());
    this.control = new HashSet<String>(params.control().keys());
    this.experiment = new HashSet<String>(params.experiment().keys());
    this.method = params.method();
    this.params = params;
    return this;
  }

  private @Result Collection<EdgeEntry> result;

  public EdgeBuilder () {
    super ("edger");
  }

  @Override
  protected Edge result () {
    return new Edge (params, result);
  }
}
