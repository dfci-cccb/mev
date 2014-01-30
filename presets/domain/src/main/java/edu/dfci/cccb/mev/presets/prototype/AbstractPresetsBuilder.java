package edu.dfci.cccb.mev.presets.prototype;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetsBuilder;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.dal.TsvReader;
import edu.dfci.cccb.mev.presets.dal.TsvReaderMetaModel;

public abstract class AbstractPresetsBuilder implements PresetsBuilder{
//  @Override
//  public List<Preset> getAll(URL data) throws PresetException{
//    CsvDataContext sourceDataContext = new CsvDataContext (data, new CsvConfiguration (1, true, false));                                                     
//    Schema csvSchema = sourceDataContext.getDefaultSchema();
//    
//    Table[] tables = csvSchema.getTables();
//    assert tables.length == 1;
//    Table sourceTable = tables[0];
//    
//    List<Preset> result = new ArrayList<Preset> ();
//    DataSet dataset = sourceDataContext.materializeMainSchemaTable (sourceTable, sourceTable.getColumns (), 0);
//    while(dataset.next ()){
//      Row row = dataset.getRow ();
//      result.add(createPreset (row.getValues ()));
//    }    
//    dataset.close ();
//    return result;
//  }

  @Override
  public List<Preset> getAll(URL data) throws PresetException{
    TsvReader reader = new TsvReaderMetaModel ();
    reader.init (data);
    
    List<Preset> result = new ArrayList<Preset> ();
    for(Object[] row : reader.readAll ())      
      result.add(createPreset (row));
    
    return result;
  }

  
}