package edu.dfci.cccb.mev.t_test.domain.impl;

import static edu.dfci.cccb.mev.t_test.domain.prototype.AbstractTTestBuilder.FULL_FILENAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.t_test.domain.prototype.AbstractTTest;

public class FileBackedTTest extends AbstractTTest implements AutoCloseable{

  
  private @Getter final File full;
  private @Getter final TemporaryFolder tempFolder;

  public FileBackedTTest (TemporaryFolder tempFolder) {
    this.tempFolder = tempFolder;
    this.full = new File (this.tempFolder, FULL_FILENAME);
  }
  
  @Override
  public Iterable<Entry> fullResults () {
    return iterateEntries (full);
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
    return new SimpleEntry (Double.parseDouble (split[1]),split[0]);
  }

  
  /* (non-Javadoc)
   * @see java.lang.Object#finalize() */
  @Override
  protected void finalize () throws Throwable {
    close ();
  }
  @Override
  public void close () throws Exception {
    tempFolder.close ();
  }

}
