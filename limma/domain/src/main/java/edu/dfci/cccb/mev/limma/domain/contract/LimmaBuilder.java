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

import edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.limma.domain.contract.Limma.Species;

/**
 * @author levk
 * 
 */
public interface LimmaBuilder extends AnalysisBuilder<LimmaBuilder, Limma> {

  LimmaBuilder control (Selection selection);

  LimmaBuilder experiment (Selection selection);

  LimmaBuilder species (Species species);

  LimmaBuilder go (String go);

  LimmaBuilder test (String test);
}
