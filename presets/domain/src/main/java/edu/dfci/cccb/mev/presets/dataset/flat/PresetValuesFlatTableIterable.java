package edu.dfci.cccb.mev.presets.dataset.flat;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jooq.impl.DSL.fieldByName;
import static org.jooq.impl.DSL.tableByName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;

import org.javatuples.Triplet;
import org.javatuples.Tuple;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.Table;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidCoordinateException;
import edu.dfci.cccb.mev.dataset.domain.contract.Value;
import edu.dfci.cccb.mev.dataset.domain.contract.Values;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleValue;
import edu.dfci.cccb.mev.presets.contract.PresetValues;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.util.timer.Timer;
@Log4j
public class PresetValuesFlatTableIterable implements PresetValues, Iterable<Value>, AutoCloseable {

  private static final TimeUnit DURATION_UNIT = SECONDS;
  private static final long DURATION = 10;
  private final LoadingCache<CacheKey, Double> CACHE;
  
  //cache
  private Map<String, Object> lastMap;
  @SuppressWarnings("unused") private Record lastRecord;
  private String lastRowKey=null;
  
  //fields
  private final @Inject @Named("presets-jooq-context") DSLContext context;  
  @SuppressWarnings("unused") private final Dimension columns;
  private final Dimension rows;
  private final String tableName;
  private final List<Field<String>>fieldList;
  private final Table<Record> table;
  private final Field<String> fieldRowId = fieldByName (String.class, "COLUMN0");

  private final PresetValuesQueryHelper queryHelper;
  //TODO: change scale to enum
  public PresetValuesFlatTableIterable (DSLContext context, String tableName, Dimension columns, Dimension rows) {
    this(context, tableName, columns, rows, "");
  }
  
  public PresetValuesFlatTableIterable (DSLContext context, String tableName, Dimension columns, Dimension rows, String scale) {
    log.debug ("*** ValueStore Iterable ***");
    
    CACHE = newBuilder ().expireAfterAccess (DURATION, DURATION_UNIT)
            .maximumSize (1000)
            .build (
              new CacheLoader<CacheKey, Double> () {
                @Override
                public Double load (CacheKey key) throws Exception {                
                  return getInternal (key.getRow (), key.getColumn ());
                }
            });
    
    this.context=context;    
    this.columns=columns;
    this.rows=rows;
    this.tableName = tableName;
    this.queryHelper = new PresetValuesQueryHelper (context);
    this.fieldList = createFieldList (columns.keys ());
    this.table = tableByName (tableName);
  }
  
  @Synchronized
  private void setLastRecord(Record record){    
    this.lastRecord=record;
    this.lastMap=record.intoMap ();
    this.lastRowKey=(String)this.lastMap.get ("COLUMN0");
  }
  @Synchronized
  private boolean isSameRecord(String row){
    if(this.lastRowKey!=null && this.lastRowKey.equals (row))
      return true;
    return false;
  }
  
  @Override 
  @SneakyThrows({PresetException.class})
  public Iterator<Value> iterator () {
    return new PresetValueIterator();
  }

  private class PresetValueIterator implements Iterator<Value>, AutoCloseable{
    private final Cursor<Record> cursor;
    private Record curRecord;
    private Iterator<Field<String>> fields;
    
    private PresetValueIterator() throws PresetException{
      cursor = openCursor ();
      initStartOfRecord();
    }    
    
    @Override
    public boolean hasNext () {
      return hasNextRow () || hasNextField ();
    }
    private Cursor<Record> openCursor(){
      Timer timer = Timer.start ("open-cursor");
      Cursor<Record> cursor;
      ResultQuery<Record> query=null;
      try{
//        query = queryHelper.queryAllRows (fieldList, table);
        query = queryHelper.queryINRows (fieldList, rows.keys (), table);
        cursor = query.fetchLazy ();
      }finally{
        if(query!=null) query.close();            
      }
      timer.read ();
      return cursor;
    }
    
    private boolean hasNextRow(){
      return cursor.hasNext ();
    }
    private boolean hasNextField(){
      return fields.hasNext ();
    }
    
    private Record fetchNextRow() throws PresetException{
      if(cursor.hasNext ())
        return cursor.fetchOne ();
      else
        throw new PresetException ("Iterator past last value: tableName="+tableName);
    }
    private Field<String> fetchNextField() throws PresetException{
      if(fields.hasNext ()==false)
         initStartOfRecord ();      
      return fields.next ();
    }
    
    private void initStartOfRecord() throws PresetException{
      fields = fieldList.iterator ();
      //first field is the row id, skip
      fields.next ();
      curRecord = fetchNextRow ();
    }
    
    @Override @SneakyThrows({PresetException.class})
    public Value next () {
      if(hasNext()){        
        Field<String> field = fetchNextField ();        
        String row = (String)curRecord.getValue (fieldRowId);
        String column = field.getName ();
        String sValue = (String)curRecord.getValue (field);
        double value=NaN;
        if ("Inf".equalsIgnoreCase (sValue))
          value = POSITIVE_INFINITY;
        else if ("-Inf".equalsIgnoreCase (sValue))
          value = NEGATIVE_INFINITY;
        else if ("NA".equalsIgnoreCase (sValue))
          value = NaN;
        else if ("null".equalsIgnoreCase (sValue))
          value = NaN;
        else if (sValue==null)
          value = NaN;
        else{
          try{
            value = Double.parseDouble (sValue);
          }catch(NumberFormatException e){
            throw new PresetException ("Cannot convert to double value:"+sValue,e);
          }
        }
        
        return new SimpleValue(row, column, value);
      }else{
        throw new PresetException ("Iterator past last value: tableName="+tableName);
      }
    }

    @Override
    public void remove () {
      throw new UnsupportedOperationException();
      
    }

    @Override
    public void close () throws Exception {
      if(cursor!=null){
        if(log.isDebugEnabled ())
          log.debug ("***closing cursor");
        cursor.close ();
      }
    }
    
    @SneakyThrows
    public void finalize(){
      close();
    }
  }
    
  @Override
  public double get (String row, String column) throws InvalidCoordinateException{
    
      //not using internal cache for now
      //instead wrapping the instance of this class into SharedCacheValue 
      //upon creation in the PresetDatasetBuilderFlatTableDB.
      //can switch to local cache later
      //return this.CACHE.get(new CacheKey (row, column));
      return getInternal (row, column);
    
  }
  
  @EqualsAndHashCode
  @RequiredArgsConstructor
  private static class CacheKey{
    @Getter private final String row;
    @Getter private final String column;    
  }  
  private double getInternal (String row, String column) throws InvalidCoordinateException {
    try {
      
      ResultQuery<?> query=null;
      if(!isSameRecord (row)){        
        try{
          query =queryHelper.queryValue (row, column, table, fieldRowId);
        //  log.debug ("PresetValuesFlatTable sql:"+query.getSQL ());      
          Record record = query.fetchOne ();          
          setLastRecord (record);
        }finally{
          if(query!=null) 
            query.close();            
        }
      } 
      String value = (String)this.lastMap.get(column);
      
      if ("Inf".equalsIgnoreCase (value))
        return POSITIVE_INFINITY;
      else if ("-Inf".equalsIgnoreCase (value))
        return NEGATIVE_INFINITY;
      else if ("NA".equalsIgnoreCase (value))
        return NaN;
      else if ("null".equalsIgnoreCase (value))
        return NaN;
      else if (value==null)
        return NaN;
      else{
        return Double.parseDouble (value);        
      }
      
    } catch (RuntimeException e) {
      throw new RuntimeException (" Failed to fetch row " + row + ", column " + column + " from " + table, e);
    }
  }

  private List<Field<String>> createFieldList(List<String> columns){
    List<Field<String>> selectFields = new ArrayList<Field<String>>(columns.size ());
    selectFields.add (fieldRowId);
    for(String columnName : columns)
      selectFields.add (fieldByName(String.class, columnName));
    return selectFields;
  }

  @Override
  public void close () throws Exception {
        //nothing to close;
  }


}
