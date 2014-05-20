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
package edu.dfci.cccb.mev.dataset.domain.fs;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import edu.dfci.cccb.mev.dataset.domain.contract.Values;

public class FlatFileValuesTest {

  @Test
  public void builder () throws Exception {
    try (FlatFileValueStoreBuilder builder = new FlatFileValueStoreBuilder ()) {
      
      builder.add (.1, "r1", "c1");
      builder.add (.2, "r1", "c2");
      builder.add (.3, "r2", "c1");
      builder.add (.4, "r2", "c2");
      builder.add (.5, "r3", "c1");
      builder.add (.6, "r3", "c2");

      Map<String, Integer> rowKeys = new LinkedHashMap<String, Integer> (2);
      rowKeys.put ("r1", 0);
      rowKeys.put ("r2", 1);
      rowKeys.put ("r3", 2);
      
      Map<String, Integer> columnKeys = new LinkedHashMap<String, Integer> (2);
      columnKeys.put ("c1", 0);
      columnKeys.put ("c2", 1);
      
      Values values = builder.build (rowKeys, columnKeys);

      assertThat (values.get ("r1", "c1"), is (.1));
      assertThat (values.get ("r2", "c1"), is (.3));
      assertThat (values.get ("r3", "c1"), is (.5));
      assertThat (values.get ("r1", "c2"), is (.2));
      assertThat (values.get ("r2", "c2"), is (.4));
      assertThat (values.get ("r3", "c2"), is (.6));
    }
  }
}
