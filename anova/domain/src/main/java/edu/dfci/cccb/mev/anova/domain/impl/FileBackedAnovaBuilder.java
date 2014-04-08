package edu.dfci.cccb.mev.anova.domain.impl;

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
import edu.dfci.cccb.mev.anova.domain.contract.Anova;
import edu.dfci.cccb.mev.anova.domain.prototype.AbstractAnovaBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.SelectionNotFoundException;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;

@Log4j
public class FileBackedAnovaBuilder extends AbstractAnovaBuilder {

  @Override
  public Anova build () throws DatasetException {

    Dimension dimension = null;
    for (Type of : values ())
      try {
        for (String selectionName : groupSelections ())
          dataset ().dimension (of).selections ().get (selectionName);
        dimension = dataset ().dimension (of);
      } catch (InvalidDimensionTypeException | SelectionNotFoundException e) {}
    if (dimension == null)
      throw new AnovaConfigurationException ();

    try {
      TemporaryFolder tempAnovaFolder = new TemporaryFolder ();

      log.debug ("Using " + tempAnovaFolder.getAbsolutePath () + " for Anova analysis");

      try {
        File datasetFile = new File (tempAnovaFolder, DATASET_FILENAME);
        try (OutputStream datasetOut = new FileOutputStream (datasetFile)) {
          composerFactory ().compose (dataset ()).write (datasetOut);
        }

        // write the configuration file.
        File configFile = new File (tempAnovaFolder, CONFIGURATION_FILENAME);
        try (PrintStream configOut = new PrintStream (new FileOutputStream (configFile))) {

          int groupId = 0;
          for (String selectionName : groupSelections ()) {
            for (String key : dimension.selections ().get (selectionName).keys ()) {
              configOut.println (key + "\t" + groupId);
            }
            groupId++;
          }
        }

        File fullOutputFile = new File (tempAnovaFolder, FULL_FILENAME);

        // write lines with paths, etc. to the appropriate file locations INTO
        // the R file (injection of a different style...)
        try (ByteArrayOutputStream script = new ByteArrayOutputStream ();
             PrintStream printScript = new PrintStream (script)) {
          printScript.println ("INFILE=\"" + datasetFile.getAbsolutePath () + "\"");
          printScript.println ("SAMPLE_FILE=\"" + configFile.getAbsolutePath () + "\"");
          printScript.println ("OUTFILE=\"" + fullOutputFile.getAbsolutePath () + "\"");
          printScript.println ("DELIMITER=\"" + PAIRING_DELIMITER + "\"");
          if (multipleTestCorrectionFlag ()) {
            printScript.println (CORRECT_FOR_MULTIPLE_TESTING + "=TRUE");

          }
          else {
            printScript.println (CORRECT_FOR_MULTIPLE_TESTING + "=FALSE");
          }

          try (InputStream tTestScript = getClass ().getResourceAsStream ("/mev_anova.R")) {
            for (int c; (c = tTestScript.read ()) >= 0; printScript.write (c));
            printScript.flush ();

            try (Reader injectedScript = new InputStreamReader (new ByteArrayInputStream (script.toByteArray ()))) {

              r ().eval (injectedScript);

              if (!log.isDebugEnabled ()) {
                datasetFile.delete ();
                configFile.delete ();
              }

              return new FileBackedAnova (tempAnovaFolder).name (name ()).type (type ());
            } catch (ScriptException e) {
              if (log.isDebugEnabled ())
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
                     Writer debug = new BufferedWriter (new OutputStreamWriter (buffer));
                     Reader datasetReader = new FileReader (datasetFile);
                     Reader configReader = new FileReader (configFile)) {
                  debug.write ("Anova script failed\nInput dataset:\n");
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
      } catch (IOException | RuntimeException | ScriptException e) {
        try {
          if (!log.isDebugEnabled ())
            tempAnovaFolder.close ();
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
