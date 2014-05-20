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

import static java.lang.Math.min;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidCoordinateException;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractValues;

/**
 * @author levk
 * 
 */
@Log4j
public class FlatFileValues extends AbstractValues implements Closeable {

  private static final int MAPPING_SIZE = 1 << 30;

  private final File file;

  private final int width;

  private final Map<String, Integer> rows;
  private final Map<String, Integer> columns;
  private final List<MappedByteBuffer> mappings = new ArrayList<> ();

  public FlatFileValues (File file, Map<String, Integer> rows, Map<String, Integer> columns, int height, int width) throws FileNotFoundException,
                                                                                                                   IOException {
    this.file = file;
    this.rows = rows;
    this.columns = columns;
    this.width = width;

    long size = 8L * height * width;
    try (RandomAccessFile access = new RandomAccessFile (file, "r")) {
      for (long offset = 0; offset < size; offset += MAPPING_SIZE)
        mappings.add (access.getChannel ().map (READ_ONLY, offset, min (size - offset, MAPPING_SIZE)));
    }
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.dataset.domain.contract.Values#get(java.lang.String,
   * java.lang.String) */
  @Override
  public double get (String row, String column) throws InvalidCoordinateException {
    int x = columns.get (column);
    int y = rows.get (row);
    long position = (((long) y) * width + x) * 8;
    return mappings.get ((int) (position / MAPPING_SIZE)).getDouble ((int) (position % MAPPING_SIZE));
  }

  /* (non-Javadoc)
   * @see java.io.Closeable#close() */
  @Override
  public void close () throws IOException {
    mappings.clear (); // gc will do the closing
    if (file instanceof Closeable) {
      log.debug ("Colsing file" + file.getAbsolutePath ());
      ((Closeable) file).close ();
    }
  }
}
