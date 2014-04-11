package edu.dfci.cccb.mev.presets.dataset;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.Analyses;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.Parser;
import edu.dfci.cccb.mev.dataset.domain.contract.RawInput;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.Values;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDataset;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDimension;
import edu.dfci.cccb.mev.dataset.domain.tsv.UrlTsvInput;
import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetDatasetBuilder;
import edu.dfci.cccb.mev.presets.contract.PresetDescriptor;
import edu.dfci.cccb.mev.presets.contract.PresetValuesStoreBuilderFactory;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;

@Log4j
@Accessors(fluent=false, chain=true)
public class PresetDatasetBuilderByJooq extends AbstractDatasetBuilder implements PresetDatasetBuilder {

  private @Getter @Setter @Inject PresetValuesStoreBuilderFactory valueStoreInjector;
  
  public PresetDatasetBuilderByJooq (PresetValuesStoreBuilderFactory valueStoreInjector) {
    this.valueStoreInjector=valueStoreInjector;
  }

  @Override
  public Dataset build (PresetDescriptor descriptor, String datasetName, Selection columnSelection, Selection rowSelection) throws PresetException {
    try{
      if(log.isDebugEnabled ())
        log.debug ("Creating preset dataset");
      
      RawInput content = new UrlTsvInput (descriptor.dataUrl ());
      Parser parser;
      for (parser = parser (content); parser.next ();) {}
      
      Dimension rows;
      
      if(columnSelection!=null)
        rows = new SimpleDimension (Dimension.Type.ROW, rowSelection.keys (), super.selections (), super.annotation ());
      else
        rows = new SimpleDimension (Dimension.Type.ROW, parser.rowKeys (), super.selections (), super.annotation ());

      Dimension columns;
      if(columnSelection!=null)
        columns = new SimpleDimension (Dimension.Type.COLUMN, columnSelection.keys (), super.selections (), super.annotation ());
      else
        columns = new SimpleDimension (Dimension.Type.COLUMN, parser.columnKeys (), super.selections (), super.annotation ());

      
      if(log.isDebugEnabled ())
        log.debug ("columns="+columns);
      if(log.isDebugEnabled ())
        log.debug ("rows="+rows);
      
      return aggregate (datasetName, valueStoreInjector.create (descriptor.name ()).build (), super.analyses (), columns, rows);
      
    }catch(InvalidDatasetNameException|DatasetBuilderException e){
      throw new PresetException ("Error creating dataset '"+datasetName+"; preset="+descriptor+" columnSelection="+columnSelection, e);
      
    }
    
  }

  @Override
  public Dataset build (Preset preset, String datasetName, Selection columnSelection, Selection rowSelection) throws PresetException {
    return build(preset.descriptor (), datasetName, columnSelection, rowSelection);
  }
  
  @Override
  protected Dataset aggregate (String name, Values values, Analyses analyses, Dimension... dimensions) throws DatasetBuilderException,
                                                                                                      InvalidDatasetNameException {    
    return new SimpleDataset (name, values, analyses, dimensions);
  }

}
