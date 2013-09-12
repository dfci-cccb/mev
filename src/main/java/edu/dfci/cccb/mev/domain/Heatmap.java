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
package edu.dfci.cccb.mev.domain;

import static edu.dfci.cccb.mev.domain.MatrixAnnotation.Meta.CATEGORICAL;
import static edu.dfci.cccb.mev.domain.MatrixAnnotation.Meta.QUANTITATIVE;
import static edu.dfci.cccb.mev.domain.MatrixData.EMPTY_MATRIX_DATA;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.supercsv.prefs.CsvPreference.TAB_PREFERENCE;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;

import us.levk.math.linear.HugeRealMatrix;

/**
 * @author levk
 * 
 */
@ToString
@Log4j
public class Heatmap implements Closeable {

  private final UUID universalId = randomUUID ();

  private RealMatrix data;
  private @Getter MatrixSummary summary;
  private List<Map<String, ?>> rowAnnotations;
  private List<Map<String, ?>> columnAnnotations;
  private List<Map<String, Map<String, String>>> rowSelections = new SelectionHolderList ();
  private List<Map<String, Map<String, String>>> columnSelections = new SelectionHolderList ();

  /**
   * Constructs empty heatmap; this is not very useful as the Heatmap object is
   * immutable
   */
  protected Heatmap () {}

  /**
   * Gets subset of the data
   * 
   * @param startRow
   * @param endRow
   * @param startColumn
   * @param endColumn
   * @return
   */
  public MatrixData getData (int startRow, int endRow, int startColumn, int endColumn) {
    if (startRow >= data.getRowDimension () || startColumn >= data.getColumnDimension ())
      return EMPTY_MATRIX_DATA;
    startRow = max (startRow, 0);
    startRow = min (startRow, data.getRowDimension () - 1);
    endRow = max (endRow, startRow);
    endRow = min (endRow, data.getRowDimension () - 1);
    startColumn = max (startColumn, 0);
    startColumn = min (startColumn, data.getColumnDimension () - 1);
    endColumn = max (endColumn, startColumn);
    endColumn = min (endColumn, data.getColumnDimension () - 1);
    return new MatrixData (data.getSubMatrix (startRow, endRow, startColumn, endColumn));
  }

  /**
   * Get available row annotation types
   * 
   * @return
   */
  public Collection<String> getRowAnnotationTypes () {
    return getAnnotationTypes (rowAnnotations);
  }

  /**
   * Get available column annotation types
   * 
   * @return
   */
  public Collection<String> getColumnAnnotationTypes () {
    return getAnnotationTypes (columnAnnotations);
  }

  /**
   * Get subset of row annotations
   * 
   * @param start
   * @param end
   * @param type
   * @return
   */
  public List<MatrixAnnotation<?>> getRowAnnotation (int startIndex, int endIndex,
                                                     String type) throws AnnotationNotFoundException {
    return getAnnotation (rowAnnotations, startIndex, endIndex, type);
  }

  /**
   * Get subset of column annotations
   * 
   * @param start
   * @param end
   * @param type
   * @return
   */
  public List<MatrixAnnotation<?>> getColumnAnnotation (int startIndex, int endIndex,
                                                        String type) throws AnnotationNotFoundException {
    return getAnnotation (columnAnnotations, startIndex, endIndex, type);
  }

  /**
   * Get all row selection ids
   * 
   * @return
   */
  public Collection<String> getRowSelectionIds () {
    return getSelectionIds (rowSelections);
  }

  /**
   * Get all column selection ids
   * 
   * @return
   */
  public Collection<String> getColumnSelectionIds () {
    return getSelectionIds (columnSelections);
  }

  /**
   * Get row selection indecis
   * 
   * @param id
   * @param start
   * @param end
   * @return
   */
  public MatrixSelection getRowSelection (String id, int start, int end) {
    return getSelection (rowSelections, start, end, id);
  }

  /**
   * Get column selection indecis
   * 
   * @param id
   * @param start
   * @param end
   * @return
   */
  public MatrixSelection getColumnSelection (String id, int start, int end) {
    return getSelection (columnSelections, start, end, id);
  }

  /**
   * Set row selection
   * 
   * @param id
   * @param selection
   * @throws IndexOutOfBoundsException
   */
  public void setRowSelection (String id, MatrixSelection selection) throws IndexOutOfBoundsException {
    setSelection (rowSelections, id, selection);
  }

  /**
   * Set columns selection
   * 
   * @param id
   * @param selection
   * @throws IndexOutOfBoundsException
   */
  public void setColumnSelection (String id, MatrixSelection selection) throws IndexOutOfBoundsException {
    setSelection (columnSelections, id, selection);
  }

  /**
   * Delete row selection
   * 
   * @param id
   */
  public void deleteRowSelection (String id) {
    deleteSelection (rowSelections, id);
  }

  /**
   * Delete column selection
   * 
   * @param id
   */
  public void deleteColumnSelections (String id) {
    deleteSelection (columnSelections, id);
  }

  /* (non-Javadoc)
   * @see java.io.Closeable#close() */
  @Override
  public void close () throws IOException {
    if (data instanceof Closeable)
      ((Closeable) data).close ();
  }

  @Accessors (fluent = true, chain = true)
  public static class Builder {
    private @Getter @Setter boolean allowComments = false;
    private @Getter @Setter boolean allowEmptyLines = false;
    private @Getter @Setter boolean assumeSingleColumnAnnotation = true;
    private @Getter @Setter CellProcessor valueProcessor = new ParseDouble ();
    private @Getter @Setter CellProcessor annotationProcessor = null;
    private @Getter @Setter String delimiterRegex = "\t";
    private @Getter @Setter List<String> columnAnnotationTypes = asList ("column");
    private @Getter @Setter List<String> rowAnnotationTypes = new AbstractList<String> () {

      @Override
      public String get (int index) {
        return "annotation-" + index;
      }

      @Override
      public int size () {
        return Integer.MAX_VALUE;
      }
    };

    public Heatmap build (InputStream input) throws IOException {
      BufferedReader reader = new BufferedReader (new InputStreamReader (input));
      String[] fields = reader.readLine ().split (delimiterRegex);
      if (log.isDebugEnabled ())
        log.debug ("Parsing matrix with header: " + Arrays.toString (fields));
      final CellProcessor[] processors = new CellProcessor[fields.length];
      int index = 0;
      for (; index < fields.length && "".equals (fields[index]); index++)
        processors[index] = annotationProcessor;
      final int lastRowAnnotationIndex = index;
      final List<Map<String, ?>> columnAnnotations = new ArrayList<Map<String, ?>> ();
      final List<Map<String, ?>> rowAnnotations = new ArrayList<Map<String, ?>> ();
      for (; index < fields.length; index++) {
        processors[index] = valueProcessor;
        columnAnnotations.add (new HashMap<String, String> () {
          private static final long serialVersionUID = 1L;

          private Map<String, ?> initialize (String annotation) {
            put (columnAnnotationTypes.get (0), annotation);
            return this;
          }
        }.initialize (fields[index]));
      }

      HugeRealMatrix data = null;
      try (final CsvListReader csvReader = new CsvListReader (reader, TAB_PREFERENCE)) {
        data = new HugeRealMatrix (new Iterator<Double> () {

          private Iterator<Object> current = null;

          @Override
          public void remove () {
            throw new UnsupportedOperationException ();
          }

          @Override
          public Double next () {
            if (!hasNext ())
              throw new NoSuchElementException ();
            else
              return (Double) current.next ();
          }

          @Override
          @SneakyThrows (IOException.class)
          public boolean hasNext () {
            if (current != null && current.hasNext ())
              return true;
            final List<Object> row = csvReader.read (processors);
            if (row == null)
              return false;
            rowAnnotations.add (new HashMap<String, String> () {
              private static final long serialVersionUID = 1L;

              {
                for (int index = 0; index < lastRowAnnotationIndex; index++)
                  put (rowAnnotationTypes.get (index), row.get (index).toString ());
              }
            });
            current = row.subList (lastRowAnnotationIndex, row.size ()).iterator ();
            return true;
          }
        }, index - lastRowAnnotationIndex);
        Heatmap result = new Heatmap ();
        result.data = data;
        result.summary = new MatrixSummary (data.getRowDimension (),
                                            data.getColumnDimension (),
                                            data.walkInOptimizedOrder (new RealMatrixPreservingVisitor () {

                                              private double max;

                                              @Override
                                              public void visit (int row, int column, double value) {
                                                if (max < value)
                                                  max = value;
                                              }

                                              @Override
                                              public void start (int rows,
                                                                 int columns,
                                                                 int startRow,
                                                                 int endRow,
                                                                 int startColumn,
                                                                 int endColumn) {
                                                max = -Double.MAX_VALUE;
                                              }

                                              @Override
                                              public double end () {
                                                return max;
                                              }
                                            }),
                                            data.walkInOptimizedOrder (new RealMatrixPreservingVisitor () {

                                              private double min;

                                              @Override
                                              public void visit (int row, int column, double value) {
                                                if (min > value)
                                                  min = value;
                                              }

                                              @Override
                                              public void start (int rows,
                                                                 int columns,
                                                                 int startRow,
                                                                 int endRow,
                                                                 int startColumn,
                                                                 int endColumn) {
                                                min = Double.MAX_VALUE;
                                              }

                                              @Override
                                              public double end () {
                                                return min;
                                              }
                                            }));
        result.rowAnnotations = rowAnnotations;
        result.columnAnnotations = columnAnnotations;
        return result;
      } catch (RuntimeException | Error e) {
        if (data != null)
          data.close ();
        throw e;
      }
    }
  }

  private Collection<String> getAnnotationTypes (List<Map<String, ?>> dimmension) {
    Set<String> result = new HashSet<String> ();
    for (Map<String, ?> entry : dimmension)
      result.addAll (entry.keySet ());
    return result;
  }

  @SuppressWarnings ({ "rawtypes", "unchecked" })
  private List<MatrixAnnotation<?>> getAnnotation (List<Map<String, ?>> dimension,
                                                   int startIndex, int endIndex,
                                                   String type) throws AnnotationNotFoundException {
    endIndex = max (endIndex, 0);
    endIndex = min (endIndex, dimension.size () - 1);
    startIndex = max (0, startIndex);
    startIndex = min (startIndex, endIndex);
    List<MatrixAnnotation<?>> result = new ArrayList<> ();
    for (int index = startIndex; index <= endIndex; index++) {
      Number min = Double.MAX_VALUE;
      Number max = Double.MIN_VALUE;
      Set<Object> categorical = new HashSet<> ();
      boolean isQuantitative = true;
      for (Map<String, ?> entry : dimension) {
        Object value = entry.get (type);
        if (value == null)
          throw new AnnotationNotFoundException (type);
        if (isQuantitative)
          if (value instanceof Number) {
            Number number = (Number) value;
            if (min.doubleValue () >= number.doubleValue ())
              min = number;
            if (max.doubleValue () <= number.doubleValue ())
              max = number;
          } else
            isQuantitative = false;
        categorical.add (value);
      }
      result.add (new MatrixAnnotation (type,
                                        dimension.get (index).get (type),
                                        isQuantitative ? QUANTITATIVE : CATEGORICAL,
                                        isQuantitative ? asList (min, max) : categorical));
    }
    return result;
  }

  private Collection<String> getSelectionIds (List<Map<String, Map<String, String>>> dimension) {
    Set<String> result = new HashSet<> ();
    for (Map<String, Map<String, String>> index : dimension)
      result.addAll (index.keySet ());
    return result;
  }

  private MatrixSelection getSelection (List<Map<String, Map<String, String>>> dimension, int start, int end, String id) {
    List<Integer> indecis = new ArrayList<Integer> ();
    Map<String, String> attributes = null;
    for (int index = end; --index >= start;)
      if ((attributes = dimension.get (index).get (id)) != null)
        indecis.add (index);
    return new MatrixSelection (attributes, indecis);
  }

  private void setSelection (List<Map<String, Map<String, String>>> dimension, String id, MatrixSelection selection) {
    for (int index : selection.indecis ())
      dimension.get (index).put (id, selection.attributes ());
  }

  private void deleteSelection (List<Map<String, Map<String, String>>> dimension, String id) {
    for (Map<String, Map<String, String>> selections : dimension)
      selections.remove (id);
  }

  private class SelectionHolderList extends ArrayList<Map<String, Map<String, String>>> {
    private static final long serialVersionUID = 1L;

    @Override
    @Synchronized
    public Map<String, Map<String, String>> get (int index) {
      while (size () < index)
        add (null);
      Map<String, Map<String, String>> result = super.get (index);
      if (result == null)
        set (index, result = new HashMap<String, Map<String, String>> ());
      return result;
    }
  }
}
