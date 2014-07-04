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

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.values;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;

import javax.script.ScriptException;

import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.SelectionNotFoundException;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.limma.domain.contract.InvalidLimmaConfigurationException;
import edu.dfci.cccb.mev.limma.domain.contract.Limma;
import edu.dfci.cccb.mev.limma.domain.prototype.AbstractLimmaBuilder;

/**
 * @author levk
 * 
 */
@Log4j
public class StatelessScriptEngineFileBackedLimmaBuilder extends AbstractLimmaBuilder {

  public static final String DATASET_FILENAME = "dataset.tsv";
  public static final String CONFIGURATION_FILENAME = "config.tsv";

  public static final String FULL_FILENAME = "output.tsv";
  public static final String RNK_FILENAME = "rnk.out";
  public static final String TOPGO_FILENAME = "topgo.tsv";

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.domain.contract.AnalysisBuilder#build() */
  @Override
  public Limma build () throws DatasetException {
    Dimension dimension = null;
    for (Type of : values ())
      try {
        dataset ().dimension (of).selections ().get (control ().name ());
        dataset ().dimension (of).selections ().get (experiment ().name ());
        dimension = dataset ().dimension (of);
      } catch (InvalidDimensionTypeException | SelectionNotFoundException e) {}
    if (dimension == null)
      throw new InvalidLimmaConfigurationException ();

    try {
      TemporaryFolder limma = new TemporaryFolder ();

      log.debug ("Using " + limma.getAbsolutePath () + " for limma analysis");

      try {
        File datasetFile = new File (limma, DATASET_FILENAME);
        try (OutputStream datasetOut = new FileOutputStream (datasetFile)) {
          composerFactory ().compose (dataset ()).write (datasetOut);
        }

        File configFile = new File (limma, CONFIGURATION_FILENAME);
        try (PrintStream configOut = new PrintStream (new FileOutputStream (configFile))) {
          for (int index = 0; index < dimension.keys ().size (); index++)
            if (control ().keys ().contains (dimension.keys ().get (index))
                && experiment ().keys ().contains (dimension.keys ().get (index)))
              throw new InvalidLimmaConfigurationException ();
            else if (experiment ().keys ().contains (dimension.keys ().get (index)))
              configOut.println (dimension.keys ().get (index) + "\t1");
            else if (control ().keys ().contains (dimension.keys ().get (index)))
              configOut.println (dimension.keys ().get (index) + "\t0");
            else
              configOut.println (dimension.keys ().get (index) + "\t-1");
        }

        File fullOutputFile = new File (limma, FULL_FILENAME);
        File rnkOutputFile = new File (limma, RNK_FILENAME);
        File topGoOutputFile = new File (limma, TOPGO_FILENAME);
        topGoOutputFile.createNewFile ();

        try (ByteArrayOutputStream script = new ByteArrayOutputStream ();
             PrintStream printScript = new PrintStream (script)) {
          printScript.println ("INFILE=\"" + datasetFile.getAbsolutePath () + "\"");
          printScript.println ("SAMPLE_FILE=\"" + configFile.getAbsolutePath () + "\"");
          printScript.println ("RESULT_OUT=\"" + fullOutputFile.getAbsolutePath () + "\"");
          printScript.println ("RNK_OUT=\"" + rnkOutputFile.getAbsolutePath () + "\"");
          printScript.println ("SPECIES=\"" + species ().toString () + "\"");
          printScript.println ("GO_TYPE=\"" + go () + "\"");
          printScript.println ("THRES=0.05");
          printScript.println ("TEST_TYPE=\"" + test () + "\"");
          printScript.println ("TOPGO_OUT=\"" + topGoOutputFile.getAbsolutePath () + "\"");

          try (InputStream limmaScript = getClass ().getResourceAsStream ("/limma.R")) {
            for (int c; (c = limmaScript.read ()) >= 0; printScript.write (c));
            printScript.flush ();

            try (Reader injectedScript = new InputStreamReader (new ByteArrayInputStream (script.toByteArray ()))) {

              r ().eval (injectedScript);

              if (!log.isDebugEnabled ()) {
                datasetFile.delete ();
                configFile.delete ();
              }

              return new FileBackedLimma (limma).name (name ())
                                                .type (type ())
                                                .control (control ())
                                                .experiment (experiment ());
            } catch (ScriptException e) {
              if (log.isDebugEnabled ())
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
                     Writer debug = new BufferedWriter (new OutputStreamWriter (buffer));
                     Reader datasetReader = new FileReader (datasetFile);
                     Reader configReader = new FileReader (configFile)) {
                  debug.write ("LIMMA script failed\nInput dataset:\n");
                  for (int c; (c = datasetReader.read ()) >= 0; debug.write (c));
                  debug.write ("\nConfiguration:\n");
                  for (int c; (c = configReader.read ()) >= 0; debug.write (c));
                  debug.flush ();
                  log.debug (buffer.toString (), e);
                }
              throw e;
            }
          }
        }
      } catch (IOException | InvalidLimmaConfigurationException | RuntimeException | ScriptException e) {
        try {
          if (!log.isDebugEnabled ())
            limma.close ();
        } catch (IOException e2) {
          e.addSuppressed (e2);
        }
        throw e;
      }
    } catch (IOException | ScriptException e) {
      throw new DatasetException (e);
    }
  }
}
