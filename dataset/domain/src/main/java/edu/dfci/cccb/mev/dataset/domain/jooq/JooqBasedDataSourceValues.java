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
package edu.dfci.cccb.mev.dataset.domain.jooq;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;

import edu.dfci.cccb.mev.dataset.domain.contract.InvalidCoordinateException;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractDataSourceValues;

/**
 * @author levk
 * 
 */
@ToString (exclude = "context")
@RequiredArgsConstructor
public class JooqBasedDataSourceValues extends AbstractDataSourceValues {

  private final DSLContext context;
  private final Table<?> table;
  private final Field<String> row;
  private final Field<String> column;
  private final Field<Double> value;

  @Override
  public double get (String row, String column) throws InvalidCoordinateException {
    return context.select (value)
                  .from (table)
                  .where (this.row.eq (row).and (this.column.eq (column)))
                  .fetchOne ()
                  .getValue (value);
  }

  @Override
  public void close () throws Exception {
    context.query ("DROP TABLE IF EXISTS {0}", table);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#finalize() */
  @Override
  protected void finalize () throws Throwable {
    close ();
  }
}
