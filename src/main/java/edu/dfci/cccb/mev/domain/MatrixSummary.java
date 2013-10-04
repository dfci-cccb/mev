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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author levk
 *
 */
@Accessors (fluent = true)
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class MatrixSummary {

  private final @Getter @JsonView int rows;
  private final @Getter @JsonView int columns;
  private final @Getter @JsonView double max;
  private final @Getter @JsonView double min;
  private final @Getter @JsonView boolean rowClustered;
  private final @Getter @JsonView boolean columnClustered;
}
