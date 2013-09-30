/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.controllers;

import static java.lang.Integer.MAX_VALUE;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import edu.dfci.cccb.mev.domain.AnnotationNotFoundException;
import edu.dfci.cccb.mev.domain.Heatmap;
import edu.dfci.cccb.mev.domain.HeatmapNotFoundException;
import edu.dfci.cccb.mev.domain.Heatmaps;
import edu.dfci.cccb.mev.domain.InvalidDimensionException;
import edu.dfci.cccb.mev.domain.MatrixAnnotation;
import edu.dfci.cccb.mev.domain.MatrixData;
import edu.dfci.cccb.mev.domain.MatrixSelection;
import edu.dfci.cccb.mev.domain.MatrixSummary;

/**
 * @author levk
 * 
 */
// TODO: This should go into a separate heatmap package
@Controller
@RequestMapping ("/heatmap")
@Log4j
public class HeatmapController {

  private @Autowired Heatmaps heatmaps;
  private @Autowired Heatmap.Builder heatmapBuilder;

  // Summary

  @RequestMapping (value = "/{id}/summary")
  @ResponseBody
  public MatrixSummary summary (@PathVariable ("id") String id) throws HeatmapNotFoundException {
    return heatmaps.get (id).getSummary ();
  }

  // GET

  @RequestMapping (method = GET)
  @ResponseBody
  public Collection<String> get () {
    return heatmaps.list ();
  }

  @RequestMapping (value = "/{id}/data/[{startRow:[0-9]+}:{endRow:[0-9]+},{startColumn:[0-9]+}:{endColumn:[0-9]+}]")
  @ResponseBody
  public MatrixData data (@PathVariable ("id") String id,
                          @PathVariable ("startRow") int startRow,
                          @PathVariable ("endRow") int endRow,
                          @PathVariable ("startColumn") int startColumn,
                          @PathVariable ("endColumn") int endColumn) throws HeatmapNotFoundException {
    if (log.isDebugEnabled ())
      log.debug ("Serving request for data for heatmap "
                 + id + " starting row " + startRow + " ending row " + endRow + " startingColumn " + startColumn
                 + " ending column " + endColumn);
    return heatmaps.get (id).getData (startRow, endRow, startColumn, endColumn);
  }

  @RequestMapping (value = "/{id}/data", method = GET)
  @ResponseBody
  @Deprecated
  // To be removed after 10/1/2013
  public MatrixData data (@PathVariable ("id") String id,
                          @RequestParam (value = "startRow", required = false) Integer startRow,
                          @RequestParam (value = "endRow", required = false) Integer endRow,
                          @RequestParam (value = "startColumn", required = false) Integer startColumn,
                          @RequestParam (value = "endColumn", required = false) Integer endColumn) throws HeatmapNotFoundException {
    if (log.isDebugEnabled ())
      log.debug ("Serving request for data for heatmap "
                 + id + " starting row " + startRow + " ending row " + endRow + " startingColumn " + startColumn
                 + " ending column " + endColumn);
    return (startRow == null || endRow == null || startColumn == null || endColumn == null)
                                                                                           ? heatmaps.get (id)
                                                                                                     .getData (0,
                                                                                                               MAX_VALUE,
                                                                                                               0,
                                                                                                               MAX_VALUE)
                                                                                           : heatmaps.get (id)
                                                                                                     .getData (startRow,
                                                                                                               endRow,
                                                                                                               startColumn,
                                                                                                               endColumn);
  }

  @RequestMapping (value = "/{id}/annotation/{dimension}", method = GET)
  @ResponseBody
  public Collection<String> annotationTypes (@PathVariable ("id") String id,
                                             @PathVariable ("dimension") String dimension) throws HeatmapNotFoundException,
                                                                                          InvalidDimensionException {
    if (isRow (dimension))
      return heatmaps.get (id).getRowAnnotationTypes ();
    else if (isColumn (dimension))
      return heatmaps.get (id).getColumnAnnotationTypes ();
    else
      throw new InvalidDimensionException (dimension);
  }

  @RequestMapping (value = "/{id}/annotation/{dimension}/{startIndex}-{endIndex}/{type}", method = GET)
  @ResponseBody
  public List<MatrixAnnotation<?>> annotation (@PathVariable ("id") String id,
                                               @PathVariable ("dimension") String dimension,
                                               @PathVariable ("startIndex") int startIndex,
                                               @PathVariable ("endIndex") int endIndex,
                                               @PathVariable ("type") String type) throws HeatmapNotFoundException,
                                                                                  InvalidDimensionException,
                                                                                  AnnotationNotFoundException {
    if (isRow (dimension))
      return heatmaps.get (id).getRowAnnotation (startIndex, endIndex, type);
    else if (isColumn (dimension))
      return heatmaps.get (id).getColumnAnnotation (startIndex, endIndex, type);
    else
      throw new InvalidDimensionException (dimension);
  }

  @RequestMapping (value = "/{id}/annotation/{dimension}/{index}", method = GET)
  @ResponseBody
  public List<MatrixAnnotation<?>> annotation (@PathVariable ("id") String id,
                                               @PathVariable ("dimension") String dimension,
                                               @PathVariable ("index") int index) throws HeatmapNotFoundException,
                                                                                 InvalidDimensionException,
                                                                                 AnnotationNotFoundException {
    if (isRow (dimension))
      return heatmaps.get (id).getRowAnnotation (index);
    else if (isColumn (dimension))
      return heatmaps.get (id).getColumnAnnotation (index);
    else
      throw new InvalidDimensionException (dimension);
  }

  @RequestMapping (value = "/{id}/selection/{dimension}", method = GET)
  @ResponseBody
  public Collection<String> selectionIds (@PathVariable ("id") String id,
                                          @PathVariable ("dimension") String dimension) throws HeatmapNotFoundException,
                                                                                       InvalidDimensionException {
    if (isRow (dimension))
      return heatmaps.get (id).getRowSelectionIds ();
    else if (isColumn (dimension))
      return heatmaps.get (id).getColumnSelectionIds ();
    else
      throw new InvalidDimensionException (dimension);
  }

  @RequestMapping (value = "/{hm-id}/selection/{dimension}/{s-id}", method = GET)
  @ResponseBody
  public MatrixSelection selection (@PathVariable ("hm-id") String heatmapId,
                                    @PathVariable ("dimension") String dimension,
                                    @PathVariable ("s-id") String selectionId,
                                    @RequestParam ("start") int start,
                                    @RequestParam ("end") int end) throws HeatmapNotFoundException,
                                                                  InvalidDimensionException {
    if (isRow (dimension))
      return heatmaps.get (heatmapId).getRowSelection (selectionId, start, end);
    else if (isColumn (dimension))
      return heatmaps.get (heatmapId).getColumnSelection (selectionId, start, end);
    else
      throw new InvalidDimensionException (dimension);
  }

  // POST

  @RequestMapping (params = "format=tsv", method = POST)
  @ResponseBody
  public String add (@RequestParam ("filedata") MultipartFile data) throws InvalidHeatmapFormatException {
    String name = data.getOriginalFilename ();
    String id = name;
    if (heatmaps.contains (id))
      for (int count = 1; heatmaps.contains (id = name + "-" + count); count++);
    put (id, data);
    return id;
  }

  @RequestMapping (value = "/{id}/annotation/{dimension}", method = { POST, PUT })
  @ResponseStatus (OK)
  public void annotate (@PathVariable ("id") String id,
                        @PathVariable ("dimension") String dimension,
                        @RequestParam ("filedata") MultipartFile data) throws HeatmapNotFoundException,
                                                                      InvalidDimensionException, IOException {
    if (isRow (dimension))
      heatmaps.get (id).setRowAnnotations (data.getInputStream ());
    else if (isColumn (dimension))
      heatmaps.get (id).setColumnAnnotations (data.getInputStream ());
    else
      throw new InvalidDimensionException (dimension);
  }

  @RequestMapping (value = "/{hm-id}/selection/{dimension}", method = POST)
  @ResponseBody
  public String select (@PathVariable ("hm-id") String heatmapId,
                        @PathVariable ("dimension") String dimension,
                        @RequestBody MatrixSelection selection) throws HeatmapNotFoundException,
                                                               InvalidDimensionException,
                                                               IndexOutOfBoundsException {
    String result = randomUUID ().toString ();
    select (heatmapId, dimension, result, selection);
    return result;
  }

  // PUT

  @RequestMapping (value = "/{id}", params = "format=tsv", method = PUT, produces = APPLICATION_JSON_VALUE)
  @ResponseStatus (OK)
  public void put (@PathVariable ("id") String id,
                   @RequestParam ("filedata") MultipartFile data) throws InvalidHeatmapFormatException {
    try {
      Heatmap heatmap = heatmapBuilder.build (data);
      heatmaps.put (id, heatmap);
      if (log.isDebugEnabled ())
        log.debug ("Put heatmap " + heatmap + " keyed " + id);
    } catch (IOException | RuntimeException e) {
      throw new InvalidHeatmapFormatException (e);
    }
  }

  @RequestMapping (value = "/{hm-id}/selection/{dimension}/{s-id}", method = PUT)
  @ResponseStatus (OK)
  public void select (@PathVariable ("hm-id") String heatmapId,
                      @PathVariable ("dimension") String dimension,
                      @PathVariable ("s-id") String selectionId,
                      @RequestBody MatrixSelection selection) throws HeatmapNotFoundException,
                                                             InvalidDimensionException,
                                                             IndexOutOfBoundsException {
    if (isRow (dimension))
      heatmaps.get (heatmapId).setRowSelection (selectionId, selection);
    else if (isColumn (dimension))
      heatmaps.get (heatmapId).setColumnSelection (selectionId, selection);
    else
      throw new InvalidDimensionException (dimension);
  }

  // DELETE

  @RequestMapping (value = "/{id}", method = DELETE)
  @ResponseStatus (OK)
  public void delete (@PathVariable ("id") String id) throws HeatmapNotFoundException {
    heatmaps.delete (id);
  }

  @RequestMapping (value = "/{hm-id}/selection/{dimension}/{s-id}", method = POST)
  @ResponseStatus (OK)
  public void delete (@PathVariable ("hm-id") String heatmapId,
                      @PathVariable ("dimension") String dimension,
                      @RequestParam ("s-id") String selectionId) throws HeatmapNotFoundException,
                                                                InvalidDimensionException,
                                                                IndexOutOfBoundsException {
    if (isRow (dimension))
      heatmaps.get (heatmapId).deleteRowSelection (selectionId);
    else if (isColumn (dimension))
      heatmaps.get (heatmapId).deleteColumnSelections (selectionId);
    else
      throw new InvalidDimensionException (dimension);
  }

  // Exceptions

  @ExceptionHandler ({ HeatmapNotFoundException.class, AnnotationNotFoundException.class,
                      IndexOutOfBoundsException.class })
  @ResponseStatus (NOT_FOUND)
  @ResponseBody
  public String handeNotFoundException (Exception e) {
    log.warn ("Unbound resource", e);
    return e.getLocalizedMessage ();
  }

  @ExceptionHandler (InvalidDimensionException.class)
  @ResponseStatus (BAD_REQUEST)
  @ResponseBody
  public String handeBadRequestException (InvalidDimensionException e) {
    log.warn ("Bad REST call", e);
    return e.getLocalizedMessage ();
  }

  @ExceptionHandler (InvalidHeatmapFormatException.class)
  @ResponseStatus (UNSUPPORTED_MEDIA_TYPE)
  @ResponseBody
  public String handleBadDataException (InvalidHeatmapFormatException e) {
    log.warn ("Bad upload data", e);
    return e.getLocalizedMessage ();
  }

  // Helpers

  private boolean isRow (String dimension) {
    return "row".equals (dimension);
  }

  private boolean isColumn (String dimension) {
    return "column".equals (dimension);
  }
}
