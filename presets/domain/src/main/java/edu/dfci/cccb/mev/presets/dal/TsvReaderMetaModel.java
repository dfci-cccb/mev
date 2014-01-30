package edu.dfci.cccb.mev.presets.dal;

import static org.eobjects.metamodel.csv.CsvConfiguration.DEFAULT_COLUMN_NAME_LINE;
import static org.eobjects.metamodel.csv.CsvConfiguration.DEFAULT_ESCAPE_CHAR;
import static org.eobjects.metamodel.csv.CsvConfiguration.DEFAULT_QUOTE_CHAR;
import static org.eobjects.metamodel.util.FileHelper.DEFAULT_ENCODING;

import java.net.URL;
import java.util.List;

import org.eobjects.metamodel.csv.CsvConfiguration;
import org.eobjects.metamodel.csv.CsvDataContext;
import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;

public class TsvReaderMetaModel implements TsvReader {

  private CsvDataContext sourceDataContext;
  private Table sourceTable;
  
  @Override
  public  TsvReader init(URL data){
    
    //CsvConfiguration config = new CsvConfiguration (1, true, false);
    CsvConfiguration config = new CsvConfiguration(
           DEFAULT_COLUMN_NAME_LINE, DEFAULT_ENCODING, '\t', DEFAULT_QUOTE_CHAR, DEFAULT_ESCAPE_CHAR, true, false);
                                                   
    sourceDataContext = new CsvDataContext (data, config);                                                     
    Schema csvSchema = sourceDataContext.getDefaultSchema();
    
    Table[] tables = csvSchema.getTables();
    assert tables.length == 1;
    sourceTable = tables[0];    
  
    return this;
  }
  
  @Override
  public int getColumnCount(){
    return sourceTable.getColumnCount ();
  }

  @Override
  public String[] getColumnNames(){
    return sourceTable.getColumnNames ();
  }
  
  @Override
  public List<Object[]> readAll(){    
    DataSet dataset = sourceDataContext.materializeMainSchemaTable (sourceTable, sourceTable.getColumns (), 0);
    List<Object[]> result = dataset.toObjectArrays ();
    dataset.close ();
    return result;
    
  }

 }
