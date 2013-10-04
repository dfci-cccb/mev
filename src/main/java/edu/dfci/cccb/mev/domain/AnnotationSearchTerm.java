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

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

/**
 * @author levk
 *
 */
@ToString
public class AnnotationSearchTerm {

  private @Getter @Setter @JsonView String attribute;
  private @Getter @Setter @JsonView String operator;
  private @Getter @Setter @JsonView String operand;
}
