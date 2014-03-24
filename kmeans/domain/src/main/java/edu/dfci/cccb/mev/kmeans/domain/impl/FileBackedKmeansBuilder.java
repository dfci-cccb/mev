package edu.dfci.cccb.mev.kmeans.domain.impl;

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
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans;
import edu.dfci.cccb.mev.kmeans.domain.prototype.AbstractKmeansBuilder;

@Log4j
public class FileBackedKmeansBuilder extends AbstractKmeansBuilder {

  @Override
  public Kmeans build () throws DatasetException {
    Dimension dimension = null;
    for (Type of : values ())
      try {
        dataset ().dimension (of).selections ().get (sampleSelection ().name ());
        dimension = dataset ().dimension (of);
      } catch (InvalidDimensionTypeException | SelectionNotFoundException e) {}
    if (dimension == null)
      throw new InvalidKmeansConfigurationException ();

    try {
      TemporaryFolder tempFolder = new TemporaryFolder ();

      log.debug ("Using " + tempFolder.getAbsolutePath () + " for K-means analysis");

      try {
        File datasetFile = new File (tempFolder, DATASET_FILENAME);
        try (OutputStream datasetOut = new FileOutputStream (datasetFile)) {
          composerFactory ().compose (dataset ()).write (datasetOut);
        }

        int sampleCount=0;
        File configFile = new File (tempFolder, CONFIGURATION_FILENAME);
        try (PrintStream configOut = new PrintStream (new FileOutputStream (configFile))) {
          for (int index = 0; index < dimension.keys ().size (); index++)
            if (sampleSelection ().keys ().contains (dimension.keys ().get (index))){
              configOut.println (dimension.keys ().get (index) + "\t0");
              sampleCount++;
            }
            else{
              configOut.println (dimension.keys ().get (index) + "\t-1");
            }
        }
        
        //need at least 1 sample...can this even be reached?
        if(sampleCount==0 ){
          throw new InvalidKmeansConfigurationException ();
        }

        File fullOutputFile = new File (tempFolder, FULL_FILENAME);

        try (ByteArrayOutputStream script = new ByteArrayOutputStream ();
             PrintStream printScript = new PrintStream (script)) {
          printScript.println (INFILE+"=\"" + datasetFile.getAbsolutePath () + "\"");          
          printScript.println (SAMPLE_FILE+"=\"" + configFile.getAbsolutePath () + "\"");
          printScript.println (OUTFILE+"=\"" + fullOutputFile.getAbsolutePath () + "\"");
          printScript.println (CLUSTER_COUNT+"=" + clusterCount ());

          try (InputStream kmeansScript = getClass ().getResourceAsStream ("/mev_kmeans.R")) {
            for (int c; (c = kmeansScript.read ()) >= 0; printScript.write (c));
            printScript.flush ();

            try (Reader injectedScript = new InputStreamReader (new ByteArrayInputStream (script.toByteArray ()))) {

              r ().eval (injectedScript);

              if (!log.isDebugEnabled ()) {
                datasetFile.delete ();
                configFile.delete ();
              }

              return new FileBackedKmeans(tempFolder).name (name ()).type (type ());
            } catch (ScriptException e) {
              if (log.isDebugEnabled ())
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
                     Writer debug = new BufferedWriter (new OutputStreamWriter (buffer));
                     Reader datasetReader = new FileReader (datasetFile);
                     Reader configReader = new FileReader (configFile)) {
                  debug.write ("K-means script failed\nInput dataset:\n");
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
            tempFolder.close ();
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
