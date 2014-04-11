package edu.dfci.cccb.mev.presets.dataset.flat;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.jooq.DSLContext;

import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import edu.dfci.cccb.mev.dataset.domain.contract.Analyses;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.Values;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SharedCacheValues;
import edu.dfci.cccb.mev.dataset.domain.simple.SharedCachedValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDataset;
import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetDatasetBuilder;
import edu.dfci.cccb.mev.presets.contract.PresetDescriptor;
import edu.dfci.cccb.mev.presets.contract.PresetDimensionBuilder;
import edu.dfci.cccb.mev.presets.contract.PresetValues;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;

@Log4j
@Accessors(fluent=false, chain=true)
public class PresetDatasetBuilderFlatTableDB extends AbstractDatasetBuilder implements PresetDatasetBuilder {

  private @Inject @Named("presets-datasource") DataSource dataSource;
  private @Inject @Named("presets-jooq-context") DSLContext context;
  private @Inject PresetDimensionBuilder dimensionBuilder;
  
  public PresetDatasetBuilderFlatTableDB (@Named("presets-datasource") DataSource dataSource,
                                        @Named("presets-jooq-context") DSLContext context,
                                        PresetDimensionBuilder dimensionBuilder
                                        ) throws SQLException {
    this.dataSource = dataSource;
    this.context = context;
    this.dimensionBuilder=dimensionBuilder;
  }

  private Dimension buildRows(PresetDescriptor descriptor, Selection selection){
    Dimension rows;
    if(selection!=null)
      rows = dimensionBuilder.build (Dimension.Type.ROW, descriptor, selection);
    else
      rows = dimensionBuilder.buildRows (descriptor);
    if(log.isDebugEnabled ())
      log.debug ("rows="+rows);
    return rows;
  }
  
  private Dimension buildColumns(PresetDescriptor descriptor, Selection selection){
    Dimension columns;
    if(selection!=null)
      columns = dimensionBuilder.build (Dimension.Type.COLUMN, descriptor, selection);
    else
      columns = dimensionBuilder.buildColumns (descriptor);    
    if(log.isDebugEnabled ())
      log.debug ("columns="+columns);
    return columns;
  }
  
  private Values buildValues(PresetDescriptor descriptor, Dimension rows, Dimension columns){
    return new SharedCacheValues (new PresetValuesFlatTableIterable (context, descriptor.name (), columns, rows));    
  }
  
  private Values buildLogValues(PresetDescriptor descriptor, Dimension rows, Dimension columns){
    return new SharedCacheValues (
               new PresetValuesLogScaleAdapter (
                  new PresetValuesFlatTableIterable (context, descriptor.name (), columns, rows)));    
  }
  
  @Override
  public Dataset build (PresetDescriptor descriptor, String datasetName, Selection columnSelection, Selection rowSelection) throws PresetException {
    try{
      if(log.isDebugEnabled ())
        log.debug ("Creating preset dataset from FLAT table:"+descriptor.name ());
      
      Dimension rows = buildRows(descriptor, rowSelection);
      Dimension columns = buildColumns (descriptor, columnSelection);
      Values presetValues = buildValues(descriptor, rows, columns);
      return aggregate (datasetName, presetValues, super.analyses (), columns, rows);
      
    }catch(InvalidDatasetNameException|DatasetBuilderException e){
      throw new PresetException ("Error creating dataset '"+datasetName+"; preset="+descriptor+" columnSelection="+columnSelection, e);
    }
    
  }

  @Override
  public Dataset build (Preset preset, String datasetName, Selection columnSelection, Selection rowSelection) throws PresetException {
    if(preset.scale ().equalsIgnoreCase ("log")){
      PresetDescriptor descriptor = preset.descriptor ();
      try{
        if(log.isDebugEnabled ())
          log.debug ("Creating preset dataset from FLAT(LOG) table:"+preset.descriptor ().name ());
        
        Dimension rows = buildRows(descriptor, rowSelection);
        Dimension columns = buildColumns (descriptor, columnSelection);
        Values presetValues = buildLogValues(descriptor, rows, columns);
        
        return aggregate (datasetName, presetValues, super.analyses (), columns, rows);
      }catch(InvalidDatasetNameException|DatasetBuilderException e){
        throw new PresetException ("Error creating dataset '"+datasetName+"; preset="+descriptor+" columnSelection="+columnSelection, e);
      }
    }else{
      return build(preset.descriptor (), datasetName, columnSelection, rowSelection);
    }
  }
  
  @Override
  protected Dataset aggregate (String name, Values values, Analyses analyses, Dimension... dimensions) throws DatasetBuilderException,
                                                                                                      InvalidDatasetNameException {    
    return new SimpleDataset (name, values, analyses, dimensions);
  }

}
