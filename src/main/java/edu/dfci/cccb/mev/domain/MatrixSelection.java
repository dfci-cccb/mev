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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author levk
 *
 */
@Accessors (fluent = true, chain = false)
@ExtensionMethod (Collections.class)
@ToString
@EqualsAndHashCode
public class MatrixSelection {

  private @Getter @Setter @JsonView Map<String, String> attributes = null;
  private @Getter @Setter @JsonView List<Integer> indices = null;
  
  public MatrixSelection () {}
  
  public MatrixSelection (Map<String, String> attributes, List<Integer> indecis) {
    this.attributes = attributes.unmodifiableMap ();
    this.indices = indecis.unmodifiableList ();
  }
}
