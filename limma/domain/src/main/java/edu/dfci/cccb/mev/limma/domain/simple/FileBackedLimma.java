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
package edu.dfci.cccb.mev.limma.domain.simple;

import static edu.dfci.cccb.mev.limma.domain.simple.StatelessScriptEngineFileBackedLimmaBuilder.FULL_FILENAME;
import static edu.dfci.cccb.mev.limma.domain.simple.StatelessScriptEngineFileBackedLimmaBuilder.TOPGO_FILENAME;
import static java.lang.Double.parseDouble;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.ToString;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.limma.domain.prototype.AbstractLimma;

/**
 * @author levk
 * 
 */
@ToString
public class FileBackedLimma extends AbstractLimma implements AutoCloseable {

  private @Getter final File full;
  private @Getter final File topGo;
  private @Getter final TemporaryFolder limma;

  public FileBackedLimma (TemporaryFolder limma) {
    this.limma = limma;
    this.full = new File (limma, FULL_FILENAME);
    this.topGo = new File (limma, TOPGO_FILENAME);
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.limma.domain.contract.LimmaResult#full() */
  @Override
  public Iterable<Entry> full () {
    return iterateEntries (full);
  }

  public Iterable<GoEntry> topGo () {
    return new Iterable<GoEntry> () {
      /* (non-Javadoc)
       * @see java.lang.Iterable#iterator() */
      @Override
      @SneakyThrows (IOException.class)
      public Iterator<GoEntry> iterator () {
        return new Iterator<GoEntry> () {
          private final BufferedReader reader = new BufferedReader (new FileReader (topGo));
          private String current = null;

          /* (non-Javadoc)
           * @see java.util.Iterator#hasNext() */
          @Override
          @SneakyThrows (IOException.class)
          public boolean hasNext () {
            return current == null ? (current = reader.readLine ()) != null : true;
          }

          /* (non-Javadoc)
           * @see java.util.Iterator#next() */
          @Override
          public GoEntry next () {
            hasNext ();
            final String[] split = current.split ("\t");
            GoEntry result = new GoEntry () {

              @Override
              public String term () {
                return string (1, split);
              }

              @Override
              public String significant () {
                return string (3, split);
              }

              @Override
              public double pValue () {
                return number (5, split);
              }

              @Override
              public String id () {
                return string (0, split);
              }

              @Override
              public String expected () {
                return string (4, split);
              }

              @Override
              public String annotated () {
                return string (2, split);
              }

              /* (non-Javadoc)
               * @see java.lang.Object#toString() */
              @Override
              public String toString () {
                return "{id:"
                       + id () + ", term:" + term () + ", annotated:" + annotated () + ", significant:"
                       + significant () + ", expected:" + expected () + ", pValue:" + pValue () + "}";
              }
            };
            current = null;
            return result;
          }

          /* (non-Javadoc)
           * @see java.util.Iterator#remove() */
          @Override
          public void remove () {
            throw new UnsupportedOperationException ();
          }
        };
      }
    };
  }

  private Iterable<Entry> iterateEntries (final File file) {
    return new Iterable<Entry> () {

      /* (non-Javadoc)
       * @see java.lang.Iterable#iterator() */
      @Override
      @SneakyThrows (IOException.class)
      public Iterator<Entry> iterator () {
        return new Iterator<Entry> () {

          private final BufferedReader reader = new BufferedReader (new FileReader (file));
          private String current = null;

          @Override
          @Synchronized
          @SneakyThrows (IOException.class)
          public boolean hasNext () {
            return current == null ? (current = reader.readLine ()) != null : true;
          }

          @Override
          @Synchronized
          public Entry next () {
            hasNext ();
            Entry result = parse (current);
            current = null;
            return result;
          }

          @Override
          public void remove () {
            throw new UnsupportedOperationException ();
          }
        };
      }
    };
  }

  private Entry parse (String line) {
    final String[] split = line.split ("\t");
    return new SimpleEntry (string (0, split),
                            number (1, split),
                            number (2, split),
                            number (3, split),
                            number (4, split));
  }

  private String string (int index, String[] split) {
    return split[index];
  }

  private double number (int index, String[] split) {
    return parseDouble (string (index, split));
  }

  /* (non-Javadoc)
   * @see java.lang.Object#finalize() */
  @Override
  protected void finalize () throws Throwable {
    close ();
  }

  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close() */
  @Override
  public void close () throws Exception {
    limma.close ();
  }
}
