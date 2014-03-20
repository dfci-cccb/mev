package edu.dfci.cccb.mev.t_test.rest.controllers;

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

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.t_test.domain.contract.TTestBuilder;

@RestController
@RequestMapping ("/dataset/" + DATASET_URL_ELEMENT)
@Scope (SCOPE_REQUEST)
public class TTestAnalysisController {

  private @Getter @Setter @Inject Dataset dataset;
  private @Getter @Setter @Inject @Named ("one.sample.t-test.builder") TTestBuilder oneSampleTTestBuilder;
  private @Getter @Setter @Inject @Named ("two.sample.t-test.builder") TTestBuilder twoSampleTTestBuilder;
  private @Getter @Setter @Inject @Named ("paired.t-test.builder") TTestBuilder pairedTTestBuilder;

  /*
   * t-test for the one-sample analysis
   */
  @RequestMapping (value = "/analyze/one_sample_ttest/{name}(dimension="
          + DIMENSION_URL_ELEMENT 
          + ",selection={selection}"
          + ",pval={p_value}"
          + ",mtc={mult_test_correction}"
          + ",mean={user_mean})",
  method = POST)
@ResponseStatus (OK)
public void startOneSample (final @PathVariable ("name") String name,
                   final @PathVariable ("pval") double pValue,
                   final @PathVariable ("mtc") boolean multTestCorrection,
                   final @PathVariable ("mean") double userMean,
                   final @PathVariable ("selection") Selection samplesToAnalyze) throws DatasetException {
dataset.analyses ().put (oneSampleTTestBuilder.name (name)
                 .dataset (dataset)
                 .controlSelection (samplesToAnalyze)
                 .pValue (pValue)
                 .multipleTestCorrectionFlag (multTestCorrection)
                 .oneSampleMean (userMean)
                 .build ());
}
  
  
  /*
   * t-test for the two-sample analysis
   */
  @RequestMapping (value = "/analyze/two_sample_ttest/{name}(dimension="
          + DIMENSION_URL_ELEMENT 
          + ",experiment={experiment}"
          + ",control={control}"
          + ",pval={p_value}"
          + ",mtc={mult_test_correction}"
          + ",eq_var={equal_variance})",
  method = POST)
@ResponseStatus (OK)
public void startTwoSample (final @PathVariable ("name") String name,
                   final @PathVariable ("pval") double pValue,
                   final @PathVariable ("mtc") boolean multTestCorrection,
                   final @PathVariable ("eq_var") boolean assumeEqualVariance,
                   final @PathVariable ("experiment") Selection experiment,
                   final @PathVariable ("control") Selection control) throws DatasetException {
dataset.analyses ().put (twoSampleTTestBuilder.name (name)
                 .dataset (dataset)
                 .experimentSelection (experiment)
                 .controlSelection (control)
                 .pValue (pValue)
                 .equalVarianceFlag (assumeEqualVariance)
                 .multipleTestCorrectionFlag (multTestCorrection)
                 .build ());
}
  
  /*
   * t-test for the paired analysis
   */
  @RequestMapping (value = "/analyze/paired_ttest/{name}(dimension="
          + DIMENSION_URL_ELEMENT 
          + ",experiment={experiment}"
          + ",control={control}"
          + ",pval={p_value}"
          + ",mtc={mult_test_correction})",
  method = POST)
@ResponseStatus (OK)
public void startPaired(final @PathVariable ("name") String name,
                   final @PathVariable ("pval") double pValue,
                   final @PathVariable ("mtc") boolean multTestCorrection,
                   final @PathVariable ("experiment") Selection experiment,
                   final @PathVariable ("control") Selection control) throws DatasetException {
dataset.analyses ().put (pairedTTestBuilder.name (name)
                 .dataset (dataset)
                 .experimentSelection (experiment)
                 .controlSelection (control)
                 .pValue (pValue)
                 .multipleTestCorrectionFlag (multTestCorrection)
                 .build ());
}
  
  
  
}
