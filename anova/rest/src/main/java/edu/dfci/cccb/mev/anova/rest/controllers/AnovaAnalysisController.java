package edu.dfci.cccb.mev.anova.rest.controllers;

import static edu.dfci.cccb.mev.dataset.rest.resolvers.DatasetPathVariableMethodArgumentResolver.DATASET_URL_ELEMENT;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DimensionPathVariableMethodArgumentResolver.DIMENSION_URL_ELEMENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.dfci.cccb.mev.anova.domain.contract.AnovaBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;

@RestController
@RequestMapping ("/dataset/" + DATASET_URL_ELEMENT)
@Scope (SCOPE_REQUEST)
public class AnovaAnalysisController {

  private @Getter @Setter @Inject Dataset dataset;
  private @Getter @Setter @Inject @Named ("anova.builder") AnovaBuilder anovaBuilder;
  
  
  /*
   * t-test for the one-sample analysis
   */
  @RequestMapping (value = "/analyze/anova/{name}(dimension="
          + DIMENSION_URL_ELEMENT 
          + ",selections={selections}"
          + ",pval={p_value}"
          + ",mtc={mult_test_correction})",
  method = POST)
@ResponseStatus (OK)
public void startOneSample (final @PathVariable ("name") String name,
                   final @PathVariable ("pval") double pValue,
                   final @PathVariable ("mtc") boolean multTestCorrection,
                   final @PathVariable ("selections") Selections sampleGroups) throws DatasetException {
dataset.analyses ().put (anovaBuilder.name (name)
                 .dataset (dataset)
                 .groupSelections (sampleGroups)
                 .pValue (pValue)
                 .multipleTestCorrectionFlag (multTestCorrection)
                 .build ());
}
  
}
