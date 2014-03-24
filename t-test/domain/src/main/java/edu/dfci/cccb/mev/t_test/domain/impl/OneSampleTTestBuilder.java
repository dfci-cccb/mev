package edu.dfci.cccb.mev.t_test.domain.impl;

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
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.SelectionNotFoundException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.t_test.domain.contract.InvalidOneSampleTTestConfigurationException;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;
import edu.dfci.cccb.mev.t_test.domain.prototype.AbstractTTestBuilder;
@Log4j
public class OneSampleTTestBuilder extends AbstractTTestBuilder {

  
  @Override
  public TTest build () throws DatasetException {
    
    //only need to check that we have a 'control' group-- in the one-sample test there are not two groupings like experiment and control.
    Dimension dimension = null;
    for (Type of : values ())
      try {
        dataset ().dimension (of).selections ().get (controlSelection ().name ());
        dimension = dataset ().dimension (of);
      } catch (InvalidDimensionTypeException | SelectionNotFoundException e) {}
    if (dimension == null)
      throw new InvalidOneSampleTTestConfigurationException ();
    
    
    try {
      TemporaryFolder tempTTestFolder = new TemporaryFolder ();

      log.debug ("Using " + tempTTestFolder.getAbsolutePath () + " for one-sample t-test analysis");

      try {
        File datasetFile = new File (tempTTestFolder, DATASET_FILENAME);
        try (OutputStream datasetOut = new FileOutputStream (datasetFile)) {
          composerFactory ().compose (dataset ()).write (datasetOut);
        }

        //write the configuration file.  Since this is for the one sample, we only have one "grouping" of samples
        //all the samples are assigned to group "0"
        //the final column, which designates the paired sample (for the paired t-test) is set to 'NA'
        File configFile = new File (tempTTestFolder, CONFIGURATION_FILENAME);
        try (PrintStream configOut = new PrintStream (new FileOutputStream (configFile))) {
          for (int index = 0; index < dimension.keys ().size (); index++){
            if (controlSelection ().keys ().contains (dimension.keys ().get (index)))
              configOut.println (dimension.keys ().get (index) + "\t0\tNA");
            else
              configOut.println (dimension.keys ().get (index) + "\t-1\tNA");
          }
        }

        File fullOutputFile = new File (tempTTestFolder, FULL_FILENAME);

        //write lines with paths, etc. to the appropriate file locations INTO the R file (injection of a different style...)
        try (ByteArrayOutputStream script = new ByteArrayOutputStream ();
             PrintStream printScript = new PrintStream (script)) {
          printScript.println ("ONE_SAMPLE=\"" + ONE_SAMPLE_T_TEST+ "\"");
          printScript.println ("TWO_SAMPLE=\"" + TWO_SAMPLE_T_TEST+ "\"");
          printScript.println ("PAIRED=\"" + PAIRED_T_TEST+ "\"");
          printScript.println ("INFILE=\"" + datasetFile.getAbsolutePath () + "\"");
          printScript.println ("SAMPLE_FILE=\"" + configFile.getAbsolutePath () + "\"");
          printScript.println ("OUTFILE=\"" + fullOutputFile.getAbsolutePath () + "\"");
          printScript.println ("TEST_TYPE=\"" + ONE_SAMPLE_T_TEST + "\"");
          printScript.println (USER_MU+"=" + oneSampleMean ());
          if(multipleTestCorrectionFlag ()){
            printScript.println (CORRECT_FOR_MULTIPLE_TESTING+"=TRUE");

          }
          else{
            printScript.println (CORRECT_FOR_MULTIPLE_TESTING+"=FALSE");
          }
          
          
          try (InputStream tTestScript = getClass ().getResourceAsStream ("/mev_t_test.R")) {
            for (int c; (c = tTestScript.read ()) >= 0; printScript.write (c));
            printScript.flush ();

            try (Reader injectedScript = new InputStreamReader (new ByteArrayInputStream (script.toByteArray ()))) {

              r ().eval (injectedScript);

              if (!log.isDebugEnabled ()) {
                datasetFile.delete ();
                configFile.delete ();
              }

              return new FileBackedTTest (tempTTestFolder).name (name ()).type (type ());
            } catch (ScriptException e) {
              if (log.isDebugEnabled ())
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
                     Writer debug = new BufferedWriter (new OutputStreamWriter (buffer));
                     Reader datasetReader = new FileReader (datasetFile);
                     Reader configReader = new FileReader (configFile)) {
                  debug.write ("One-sample t-test script failed\nInput dataset:\n");
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
            tempTTestFolder.close ();
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
