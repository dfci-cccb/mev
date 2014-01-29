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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;

/**
 * @author levk
 * 
 */
@EqualsAndHashCode
@ToString
public abstract class AbstractWorkspace implements Workspace, AutoCloseable {

  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close() */
  @Override
  public void close () throws Exception {
    Exception toThrow = null;
    for (String name : list ())
      try {
        Dataset dataset = get (name);
        if (dataset instanceof AutoCloseable)
          ((AutoCloseable) dataset).close ();
      } catch (Exception e) {
        if (toThrow == null)
          toThrow = e;
        else
          toThrow.addSuppressed (e);
      }
  }
}
