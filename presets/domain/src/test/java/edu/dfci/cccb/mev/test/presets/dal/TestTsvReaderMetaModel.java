package edu.dfci.cccb.mev.test.presets.dal;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.dfci.cccb.mev.presets.dal.TsvReader;
import edu.dfci.cccb.mev.presets.dal.TsvReaderMetaModel;

public class TestTsvReaderMetaModel {

  static URL data;
  @BeforeClass
  public static void initData(){
    data = TestTsvReaderMetaModel.class.getResource ("/tcga/file_metadata.tsv");
    assertNotNull(data);
  }
  
  TsvReader tsvReader;  
  @Before
  public void initReader(){
    tsvReader = new TsvReaderMetaModel ();
    this.tsvReader.init (data);
    
  }
  
  @Test  
  public void testInit () {}

  @Test
  public void testGetColumnCount () {
    assertEquals (7, tsvReader.getColumnCount ());
  }

  @Test
  public void testGetColumnNames () {
    String[] expected = {"filename", "path", "disease", "disease name", "platform", "platform name", "data level"};
    assertArrayEquals (expected, tsvReader.getColumnNames ());;
  }

  @Test
  public void testReadAll(){
    String[] row1 = {"BRCA.MDA_RPPA_Core.Level_3.tsv","BRCA/Level_3","BRCA","Breast invasive carcinoma","MDA_RPPA_Core","M.D. Anderson Reverse Phase Protein Array Core","Level_3"};
    //String[] rowLast = {"BRCA.IlluminaHiSeq_RNASeq.Level_3.Expression-Gene.RPKM.tsv","BRCA/Level_2","BRCA","Breast invasive carcinoma","AgilentG4502A_07_3","Agilent 244K Custom Gene Expression G4502A-07-3","Level_2"};
    List<Object[]> result = tsvReader.readAll ();
    assertEquals (result.size (), 8);
    assertArrayEquals (row1, result.get (0));
    //assertArrayEquals (rowLast, result.get (result.size()-1));
    
  }
  
}
