package edu.dfci.cccb.mev.kmeans.rest.controllers;

import static edu.dfci.cccb.mev.dataset.rest.resolvers.DatasetPathVariableMethodArgumentResolver.DATASET_URL_ELEMENT;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DimensionPathVariableMethodArgumentResolver.DIMENSION_URL_ELEMENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import javax.inject.Inject;

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
import edu.dfci.cccb.mev.kmeans.domain.contract.KmeansBuilder;

@RestController
@RequestMapping ("/dataset/" + DATASET_URL_ELEMENT)
@Scope (SCOPE_REQUEST)
public class KmeansAnalysisController {
  private @Getter @Setter (onMethod = @_ (@Inject)) Dataset dataset;
  private @Getter @Setter (onMethod = @_ (@Inject)) KmeansBuilder kmeansBuilder;

  @RequestMapping (value = "/analyze/kmeans/{name}(dimension="
                           + DIMENSION_URL_ELEMENT + ",selection={selection})",
                   method = POST)
  @ResponseStatus (OK)
  public void start (final @PathVariable ("name") String name,
                     final @PathVariable ("selection") Selection selection) throws DatasetException {
    dataset.analyses ().put (kmeansBuilder.name (name)
                                  .dataset (dataset)
                                  .sampleSelection (selection)
                                  .build ());
  }
}
