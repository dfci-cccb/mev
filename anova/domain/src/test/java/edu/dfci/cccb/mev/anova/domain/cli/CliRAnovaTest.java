package edu.dfci.cccb.mev.anova.domain.cli;

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.COLUMN;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.script.ScriptEngineManager;

import lombok.extern.log4j.Log4j;

import org.junit.Test;

import edu.dfci.cccb.mev.anova.domain.contract.Anova;
import edu.dfci.cccb.mev.anova.domain.contract.Anova.Entry;
import edu.dfci.cccb.mev.anova.domain.impl.FileBackedAnovaBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.Selections;
import edu.dfci.cccb.mev.dataset.domain.mock.MapBackedValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.mock.MockTsvInput;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListSelections;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleSelection;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvComposerFactory;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvParserFactory;

@Log4j
public class CliRAnovaTest {

  private double pValTolerance=0.01;
  
  @Test
  public void one_sample_ttest_produces_correct_pValue () throws Exception {
    Dataset dataset = new SimpleDatasetBuilder ().setParserFactories (asList (new SuperCsvParserFactory ()))
                                                 .setValueStoreBuilder (new MapBackedValueStoreBuilder ())
                                                 .build (new MockTsvInput ("mock", "id\tsa\tsb\tsc\tsd\tse\tsf\n" +
                                                                                   "g1\t0.103\t0.105\t0.106\t0.104\t0.104\t0.105\n" +
                                                                                   "g2\t0.103\t0.105\t0.306\t0.304\t0.104\t0.105"));
    Selection groupA = new SimpleSelection ("groupA", new Properties (), asList ("sa", "sb"));
    Selection groupB = new SimpleSelection ("groupB", new Properties (), asList ("sc", "sd"));
    Selection groupC = new SimpleSelection ("groupC", new Properties (), asList ("se", "sf"));

    dataset.dimension (COLUMN).selections ().put (groupA);
    dataset.dimension (COLUMN).selections ().put (groupB);
    dataset.dimension (COLUMN).selections ().put (groupC);
    
    Selections allSelections=new ArrayListSelections ();
    allSelections.put (groupA);
    allSelections.put (groupB);
    allSelections.put (groupC);

    
    Anova result =
                   new FileBackedAnovaBuilder ().r (new ScriptEngineManager ().getEngineByName ("CliR"))
                        .composerFactory (new SuperCsvComposerFactory ())
                   .name ("one sample")
                 .dataset (dataset)
                 .groupSelections (allSelections)
                 .pValue (0.05)
                 .multipleTestCorrectionFlag (false)
                 .build ();
    Iterable<Entry> ie=result.fullResults ();
    assertEquals (0.74,
                  ie.iterator ().next ().pValue (),
                  pValTolerance);
    
  }
}
