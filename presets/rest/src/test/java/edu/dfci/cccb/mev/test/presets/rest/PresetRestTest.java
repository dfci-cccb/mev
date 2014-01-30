package edu.dfci.cccb.mev.test.presets.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.dfci.cccb.mev.presets.contract.Presets;
import edu.dfci.cccb.mev.presets.rest.configuration.PresetsRestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PresetsRestConfiguration.class})
public class PresetRestTest {
  
  @Inject Presets tcgaPresets;
  @Inject URL tcgaPresetsRoot;
  
  @Test
  public void testTcgaPresetsRoot ()  {
    //URL expected = new URL("file:/home/antony/work/danafarber/mev/data/tcga/tcga_data_tempSubset/");
    File checkExists = new File(tcgaPresetsRoot.getFile ());
    assert(checkExists.exists ());
  }
  
  
  @Test 
  public void testGetTcgaPresets () {
    //Preset preset = new TcgaPresetMetafile ("BRCA.MDA_RPPA_Core.Level_3.tsv", "BRCA/Level_3", disease, diseaseName, platform, platformName)
    assertNotNull (tcgaPresets);
    assertNotNull( tcgaPresets.list ());
    assertEquals(3, tcgaPresets.list ().size ());
    
  }

}
