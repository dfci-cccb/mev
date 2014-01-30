package edu.dfci.cccb.mev.test.presets.domain;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import lombok.extern.log4j.Log4j;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetDescriptor;
import edu.dfci.cccb.mev.presets.contract.Presets;
import edu.dfci.cccb.mev.presets.contract.PresetsBuilder;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetNotFoundException;
import edu.dfci.cccb.mev.test.presets.rest.configuration.PresetsRestConfigurationMock;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PresetsRestConfigurationMock.class})
@Log4j
@SuppressWarnings ("serial")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestTcgaPresets {

  @Inject Presets tcgaPresets;
  @Inject @Named("tcgaPreset") Provider<Preset> presetProvider;
  @Inject PresetsBuilder builder;
  List<Preset> expectedPresets;
  
  @Before
  public void checkPresetsLoaded() throws PresetException{
    assertNotNull(tcgaPresets);
    expectedPresets = new ArrayList<Preset> (8);
    Object[] values1 = {"BRCA.MDA_RPPA_Core.Level_3.tsv","BRCA/Level_3","BRCA","Breast invasive carcinoma","MDA_RPPA_Core","M.D. Anderson Reverse Phase Protein Array Core","Level_3"};
    expectedPresets.add (presetProvider.get().init(values1));
    Object[] values3 = {"ACC.IlluminaHiSeq_miRNASeq.Level_3.Expression-miRNA.readsPerMillionMapped.tsv","ACC/Level_3","ACC","Adrenocortical carcinoma","IlluminaHiSeq_miRNASeq","Illumina HiSeq 2000 miRNA Sequencing","Level_3"};    
    expectedPresets.add (presetProvider.get().init(values3));
    Object[] values6 = {"BRCA.IlluminaHiSeq_RNASeq.Level_3.Expression-Gene.RPKM.tsv","BRCA/Level_3","BRCA","Breast invasive carcinoma","IlluminaHiSeq_RNASeq","Illumina HiSeq 2000 RNA Sequencing","Level_3"};
    expectedPresets.add (presetProvider.get().init(values6));

  }
   
  @Test 
  public void testGet () throws PresetNotFoundException, PresetException {
    Preset actuall = tcgaPresets.get ("BRCA.IlluminaHiSeq_RNASeq.Level_3.Expression-Gene.RPKM.tsv");
    
    assertNotNull (actuall);
    
    Object[] values = {"BRCA.IlluminaHiSeq_RNASeq.Level_3.Expression-Gene.RPKM.tsv","BRCA/Level_3","BRCA","Breast invasive carcinoma","IlluminaHiSeq_RNASeq","Illumina HiSeq 2000 RNA Sequencing","Level_3"};    
    Preset expected = presetProvider.get().init (values );
    log.info ("actual:"+actuall.toString ());
    log.info ("expect:"+expected.toString ());    
    assertThat (expected, equalTo(actuall));
    
  }
  
  @Test 
  public void testGetDataFile () throws PresetNotFoundException, PresetException {
    Preset preset = tcgaPresets.get ("ACC.IlluminaHiSeq_miRNASeq.Level_3.Expression-miRNA.readsPerMillionMapped.tsv");
    assertNotNull (preset);
    
    PresetDescriptor descriptor = preset.descriptor ();
    File checkFile = new File( descriptor.dataUrl ().getFile() );
    assertTrue(checkFile.exists ());
  }

  @Test 
  public void testGetColumnFile () throws PresetNotFoundException, PresetException {
    Preset preset = tcgaPresets.get ("ACC.IlluminaHiSeq_miRNASeq.Level_3.Expression-miRNA.readsPerMillionMapped.tsv");
    assertNotNull (preset);
    
    PresetDescriptor descriptor = preset.descriptor ();
    File checkFile = new File( descriptor.columnUrl ().getFile() );
    assertTrue(checkFile.exists ());
  }
  
  @Test 
  public void testPutNew () throws PresetException {
    assertThat(expectedPresets, is(tcgaPresets.getAll ()));
    
    Object[] valuesNew = {"NEW_ITEM_TEST","BRCA/Level_2","BRCA","Breast invasive carcinoma","AgilentG4502A_07_3","Agilent 244K Custom Gene Expression G4502A-07-3","Level_2"};
    expectedPresets.add (presetProvider.get().init(valuesNew));
    tcgaPresets.put (presetProvider.get().init(valuesNew));
    assertThat(expectedPresets, is(tcgaPresets.getAll ()));
    
  }
  
  @Test 
  public void testPutExisting () throws PresetException {
    
    assertThat(expectedPresets, is(tcgaPresets.getAll ()));
   
    Object[] valuesLast = {"BRCA.IlluminaHiSeq_RNASeq.Level_3.Expression-Gene.RPKM.tsv","BRCA/Level_3","BRCA","Breast invasive carcinoma","IlluminaHiSeq_RNASeq","Illumina HiSeq 2000 RNA Sequencing","Level_3"};   
    tcgaPresets.put (presetProvider.get().init(valuesLast));
    expectedPresets.remove(expectedPresets.size ()-1);
    expectedPresets.add (presetProvider.get().init(valuesLast));
    
    assertThat(expectedPresets, is(tcgaPresets.getAll ()));
  }

  @Test 
  public void testRemove () throws PresetNotFoundException {
    assertThat(expectedPresets, is(tcgaPresets.getAll ()));
    tcgaPresets.remove ("BRCA.MDA_RPPA_Core.Level_3.tsv");
    expectedPresets.remove(0);
    assertThat(expectedPresets, is(tcgaPresets.getAll ()));
  }
  
  @Test 
  public void testGetAll () throws PresetException {
    List<Preset> actuall = tcgaPresets.getAll ();
    assertEquals (3, actuall.size ());
    
    assertThat(expectedPresets, is(actuall));
    
  }

  
  @Test
  public void testList () {
    List<String> names = tcgaPresets.list ();
    List<String> expected = new ArrayList<String>(){
            {
              add("BRCA.MDA_RPPA_Core.Level_3.tsv");
              add("ACC.IlluminaHiSeq_miRNASeq.Level_3.Expression-miRNA.readsPerMillionMapped.tsv");              
              add("BRCA.IlluminaHiSeq_RNASeq.Level_3.Expression-Gene.RPKM.tsv");
    }};
    
    assertThat (names, is(expected));
  }

  
}
