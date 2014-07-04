package edu.dfci.cccb.mev.limma.domain.cli;

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

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.COLUMN;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.script.ScriptEngineManager;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.mock.MapBackedValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.mock.MockTsvInput;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleSelection;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvComposerFactory;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvParserFactory;
import edu.dfci.cccb.mev.limma.domain.contract.Limma;
import edu.dfci.cccb.mev.limma.domain.contract.Limma.Entry;
import edu.dfci.cccb.mev.limma.domain.contract.Limma.GoEntry;
import edu.dfci.cccb.mev.limma.domain.contract.Limma.Species;
import edu.dfci.cccb.mev.limma.domain.simple.StatelessScriptEngineFileBackedLimmaBuilder;

/**
 * @author levk
 * 
 */
@Log4j
public class CliRLimmaTest {

  @Test
  public void test () throws Exception {
    try (InputStream inp = getClass ().getResourceAsStream ("/mouse_test_data.tsv");
         ByteArrayOutputStream copy = new ByteArrayOutputStream ()) {
      IOUtils.copy (inp, copy);
      Dataset dataset = new SimpleDatasetBuilder ().setParserFactories (asList (new SuperCsvParserFactory ()))
                                                   .setValueStoreBuilder (new MapBackedValueStoreBuilder ())
                                                   .build (new MockTsvInput ("mock",
                                                                             copy.toString ()));
      Selection experiment = new SimpleSelection ("experiment", new Properties (), asList ("A", "B", "C"));
      Selection control = new SimpleSelection ("control", new Properties (), asList ("D", "E", "F"));
      dataset.dimension (COLUMN).selections ().put (experiment);
      dataset.dimension (COLUMN).selections ().put (control);
      Limma result =
                     new StatelessScriptEngineFileBackedLimmaBuilder ().r (new ScriptEngineManager ().getEngineByName ("CliR"))
                                                                       .composerFactory (new SuperCsvComposerFactory ())
                                                                       .dataset (dataset)
                                                                       .control (control)
                                                                       .experiment (experiment)
                                                                       .species (Species.MOUSE)
                                                                       .go ("BP")
                                                                       .test ("Fisher test")
                                                                       .build ();
      for (Entry e : result.full ())
        log.debug ("Full limma entry: " + e);
      for (GoEntry e : result.topGo ())
        log.debug ("topGo entry: " + e);

      assertThat (result.full ().iterator ().next ().averageExpression (), closeTo (12.985, 0.001));
      assertThat (result.topGo ().iterator ().next ().pValue (), any (Double.class));
    }
  }
}
