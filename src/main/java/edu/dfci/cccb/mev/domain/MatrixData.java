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

import java.util.AbstractList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealMatrix;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author levk
 * 
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class MatrixData {

  public static final MatrixData EMPTY_MATRIX_DATA = new MatrixData (null);

  /**
   * Matrix data
   */
  private final RealMatrix data;

  /**
   * Gets number of rows
   * 
   * @return
   */
  @JsonView
  public int rows () {
    return data == null ? 0 : data.getRowDimension ();
  }

  /**
   * Gets number of columns
   * 
   * @return
   */
  @JsonView
  public int columns () {
    return data == null ? 0 : data.getColumnDimension ();
  }

  /**
   * Gets the matrix values as a single dimmensional array row by row
   * 
   * @return
   */
  @JsonView
  public List<Double> values () {
    return new AbstractList<Double> () {

      /* (non-Javadoc)
       * @see java.util.AbstractList#get(int) */
      @Override
      public Double get (int index) {
        if (data == null) throw new IndexOutOfBoundsException ();
        try {
          return data.getEntry (index / data.getColumnDimension (), index % data.getColumnDimension ());
        } catch (OutOfRangeException e) {
          throw new IndexOutOfBoundsException ();
        }
      }

      /* (non-Javadoc)
       * @see java.util.AbstractCollection#size() */
      @Override
      public int size () {
        return data == null ? 0 : data.getColumnDimension () * data.getRowDimension ();
      }
    };
  }
}
