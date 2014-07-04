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
package edu.dfci.cccb.mev.limma.domain.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;

/**
 * @author levk
 * 
 */
public interface Limma extends Analysis {

  public interface Entry {
    String id ();

    double logFoldChange ();

    double averageExpression ();

    double pValue ();

    double qValue ();
  }

  public enum Species {
    HUMAN,
    MOUSE,
    RAT;

    public String toString () {
      return super.toString ().toLowerCase ();
    }
  }

  public interface GoEntry {
    String id ();

    String term ();

    String annotated ();

    String significant ();

    String expected ();

    double pValue ();
  }

  Iterable<Entry> full ();

  Iterable<GoEntry> topGo ();

  Selection control ();

  Selection experiment ();

  Species species ();

  String go ();

  String test ();
}
