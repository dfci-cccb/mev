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
package edu.dfci.cccb.mev.dataset.domain.prototype;

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.COLUMN;
import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.ROW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.Analyses;
import edu.dfci.cccb.mev.dataset.domain.contract.Annotation;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type;
import edu.dfci.cccb.mev.dataset.domain.contract.InputContentStreamException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.Parser;
import edu.dfci.cccb.mev.dataset.domain.contract.ParserFactory;
import edu.dfci.cccb.mev.dataset.domain.contract.RawInput;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.SelectionBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;
import edu.dfci.cccb.mev.dataset.domain.contract.UnparsableContentTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.ValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Values;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListAnalyses;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListSelections;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDimension;

/**
 * @author levk
 * 
 */
@EqualsAndHashCode
@ToString
@Accessors (fluent = false, chain = true)
@Log4j
public abstract class AbstractDatasetBuilder implements DatasetBuilder {
 
  private @Getter @Setter @Inject Collection<? extends ParserFactory> parserFactories;
  private @Getter @Setter @Inject ValueStoreBuilder valueStoreBuilder;
  private @Getter @Setter @Inject SelectionBuilder selectionBuilder;

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder#build(edu.dfci
   * .cccb.mev.dataset.domain.contract.RawInput) */
  @Override
  public Dataset build (RawInput content) throws DatasetBuilderException,
                                         InvalidDatasetNameException,
                                         InvalidDimensionTypeException {
    if(log.isDebugEnabled ())
      log.debug ("Building dataset..."+content.name ());
    Parser parser;
    for (parser = parser (content); parser.next ();) {
      valueStoreBuilder.add (parser.value (), parser.projection (ROW), parser.projection (COLUMN));
//      if (!rows.containsKey (parser.projection (ROW)))
//        rows.put (parser.projection (ROW), parser.projection (ROW));
//      if (!columns.containsKey (parser.projection (COLUMN)))
//        columns.put (parser.projection (COLUMN), parser.projection (COLUMN));
    }
    
//    List<String> rowsList = new ArrayList<String>(rows.values());
//    List<String> columnsList = new ArrayList<String>(columns.values());
    
    return aggregate (content.name (), valueStoreBuilder.build (), analyses (),
                      dimension (ROW, parser.rowKeys (), selections (), annotation ()),
                      dimension (COLUMN, parser.columnKeys (), selections (), annotation ()));
  }
  
  @Override
  public Dataset build (RawInput content, Selection columnSelection) throws DatasetBuilderException,
                                                                    InvalidDatasetNameException,
                                                                    InvalidDimensionTypeException {
    Map<String, String> rows = new LinkedHashMap<String, String> ();
    Map<String, String> columns = new LinkedHashMap<String, String> ();
    if(log.isDebugEnabled ())
      log.debug ("**selection: " + columnSelection.keys ());
    for (Parser parser = parser (content); parser.next ();) {
      if(columnSelection.keys ().contains (parser.projection (COLUMN))){
        
//        if(log.isDebugEnabled ())
//          log.debug("+++adding:"+parser.projection (COLUMN));        
        
        valueStoreBuilder.add (parser.value (), parser.projection (ROW), parser.projection (COLUMN));        
        if (!rows.containsKey (parser.projection (ROW)))
          rows.put (parser.projection (ROW), parser.projection (ROW));
        if (!columns.containsKey (parser.projection (COLUMN)))
          columns.put (parser.projection (COLUMN), parser.projection (COLUMN));
      }else{
//        if(log.isDebugEnabled ())
//          log.debug ("---skipping:"+parser.projection (COLUMN));
        
      }
    }
    List<String> rowsList = new ArrayList<String>(rows.values());
    List<String> columnsList = new ArrayList<String>(columns.values());
    
    return aggregate (content.name (), valueStoreBuilder.build (), analyses (),
                      dimension (ROW, rowsList, selections (), annotation ()),
                      dimension (COLUMN, columnsList, selections (), annotation ()));
  }

  protected Analyses analyses () {
    return new ArrayListAnalyses ();
  }

  protected Dimension dimension (Type type, List<String> keys, Selections selections, Annotation annotation) {
    return new SimpleDimension (type, keys, selections, annotation);
  }

  protected Selections selections () {
    return new ArrayListSelections ();
  }

  protected Annotation annotation () {
    return null; // TODO: add annotation
  }

  protected abstract Dataset aggregate (String name,
                                        Values values,
                                        Analyses analyses,
                                        Dimension... dimensions) throws DatasetBuilderException,
                                                                InvalidDatasetNameException;

  protected Parser parser (RawInput content) throws DatasetBuilderException {
    for (ParserFactory parserFactory : parserFactories)
      if (parserFactory.contentType ().equals (content.contentType ()))
        try {
          return parserFactory.parse (content.input ());
        } catch (IOException e) {
          throw new InputContentStreamException (e);
        }
    throw new UnparsableContentTypeException ().contentType (content.contentType ());
  }
}