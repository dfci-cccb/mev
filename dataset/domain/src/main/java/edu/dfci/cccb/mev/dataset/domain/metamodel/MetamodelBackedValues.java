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
package edu.dfci.cccb.mev.dataset.domain.metamodel;

import static org.eobjects.metamodel.query.LogicalOperator.AND;
import static org.eobjects.metamodel.query.OperatorType.EQUALS_TO;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.drop.DropTable;
import org.eobjects.metamodel.query.FilterItem;
import org.eobjects.metamodel.query.SelectItem;
import org.eobjects.metamodel.schema.Table;

import edu.dfci.cccb.mev.dataset.domain.contract.InvalidCoordinateException;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractDataSourceValues;

/**
 * @author levk
 * 
 */
@ToString (of = "values")
@EqualsAndHashCode (callSuper = true)
public class MetamodelBackedValues extends AbstractDataSourceValues {

  private final Table values;
  private final UpdateableDataContext context;

  /**
   * 
   */
  public MetamodelBackedValues (Table values, UpdateableDataContext context) {
    this.values = values;
    this.context = context;
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.dataset.domain.contract.Values#get(java.lang.String,
   * java.lang.String) */
  @Override
  public double get (String row, String column) throws InvalidCoordinateException {
    try (DataSet data = query (row, column)) {
      if (data.next ())
        return value (data.getRow ().getValue (0));
      else
        throw new InvalidCoordinateException ();
    }
  }

  protected DataSet query (String row, String column) {
    return context.executeQuery (context.query ()
                                        .from (values)
                                        .select (VALUE_FIELD_NAME)
                                        .where (and (eq (ROW_FIELD_NAME, row), eq (COLUMN_FIELD_NAME, column)))
                                        .toQuery ());
  }

  private FilterItem and (FilterItem... filterItems) {
    return new FilterItem (AND, filterItems);
  }

  private FilterItem eq (String fieldName, Object value) {
    return new FilterItem (new SelectItem (values.getColumnByName (fieldName)), EQUALS_TO, value);
  }

  private double value (Object result) {
    if (result instanceof String)
      return Double.valueOf ((String) result);
    else if (result instanceof Double)
      return (Double) result;
    else
      throw new IllegalArgumentException ("Unable to coerce " + result
                                          + (result == null ? "" : (" of " + result.getClass ().getSimpleName ()))
                                          + " to a double");
  }

  /* (non-Javadoc)
   * @see java.lang.Object#finalize() */
  @Override
  protected void finalize () throws Throwable {
    close ();
  }

  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close() */
  @Override
  public void close () throws Exception {
    context.executeUpdate (new DropTable (values));
  }
}
