package edu.dfci.cccb.mev.support;

import static edu.dfci.cccb.mev.domain.MatrixAnnotation.Meta.CATEGORICAL;
import static edu.dfci.cccb.mev.domain.MatrixAnnotation.Meta.QUANTITATIVE;
import static java.util.Arrays.asList;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;

import org.eobjects.metamodel.BatchUpdateScript;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.convert.Converters;
import org.eobjects.metamodel.convert.StringToIntegerConverter;
import org.eobjects.metamodel.create.ColumnCreationBuilder;
import org.eobjects.metamodel.create.TableCreationBuilder;
import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.data.Row;
import org.eobjects.metamodel.insert.RowInsertionBuilder;
import org.eobjects.metamodel.pojo.MapTableDataProvider;
import org.eobjects.metamodel.pojo.PojoDataContext;
import org.eobjects.metamodel.pojo.TableDataProvider;
import org.eobjects.metamodel.query.FilterItem;
import org.eobjects.metamodel.query.FunctionType;
import org.eobjects.metamodel.query.LogicalOperator;
import org.eobjects.metamodel.query.OperatorType;
import org.eobjects.metamodel.query.Query;
import org.eobjects.metamodel.query.SelectItem;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.eobjects.metamodel.util.SimpleTableDef;

import edu.dfci.cccb.mev.domain.MatrixAnnotation;

@Log4j
public class AnnotationDataAccessLayer implements Closeable {
  private final UpdateableDataContext dbDataContext;
  private final String dataNamespace;
  private static final String INDEX_COL_NAME = "mev_index";

  private int reloadCounter = 0;
  private String currentTableName = "";

  public AnnotationDataAccessLayer (DataSource dataSource, String universalIdTag) {
    this.dataNamespace = universalIdTag;
    this.dbDataContext = DataContextFactory.createJdbcDataContext (dataSource);
    log.debug ("Annotations data access layer: " + dbDataContext.getDefaultSchema ().getName ());
  }

  private String generateUniqueTableName () {
    return dataNamespace + "-" + reloadCounter++;
  }

  public boolean setAnnotations (List<Map<String, ?>> data) {
    // read collections into table
    String[] colNames = data.get (0).keySet ().toArray (new String[data.get (0).keySet ().size ()]);
    TableDataProvider<?> mapProvider = new MapTableDataProvider (new SimpleTableDef ("pojo-data", colNames), data);
    DataContext pojoDataContext = new PojoDataContext (Arrays.<TableDataProvider<?>>asList (mapProvider));

    // make sure mev_index column is numeric
    Column numberCol = pojoDataContext.getColumnByQualifiedLabel (INDEX_COL_NAME);
    if (numberCol != null)
      pojoDataContext = Converters.addTypeConverter (pojoDataContext, numberCol, new StringToIntegerConverter ());

    Table pojoTable = getTableByName (pojoDataContext, "pojo-data");
    Table dbTable = getTableByName (dbDataContext, currentTableName);
    return importTable (dbDataContext, dbTable, pojoDataContext, pojoTable);
  }

  public boolean setAnnotations (InputStream data) {
    DataContext csvDataContext = DataContextFactory.createCsvDataContext (data, '\t', ' ');

    // get the one and only table (csv)
    Schema csvSchema = csvDataContext.getDefaultSchema ();
    Table[] tables = csvSchema.getTables ();
    assert tables.length == 1;
    Table table = tables[0];

    // make sure mev_index column is numeric
    Column numberCol = csvDataContext.getColumnByQualifiedLabel (INDEX_COL_NAME);
    if (numberCol != null)
      csvDataContext = Converters.addTypeConverter (csvDataContext, numberCol, new StringToIntegerConverter ());

    Table csvTable = table;
    Table dbTable = getTableByName (dbDataContext, currentTableName);
    return importTable (dbDataContext, dbTable, csvDataContext, csvTable);
  }

  public Collection<String> getColumnNames () {
    Table dbTable = getTableByName (dbDataContext, currentTableName);
    ArrayList<String> columnNames = new ArrayList<String> ();
    for (String columnName : dbTable.getColumnNames ()) {
      if (!columnName.equalsIgnoreCase (INDEX_COL_NAME))
        columnNames.add (columnName);
    }
    return columnNames;
  }

  public List<MatrixAnnotation<?>> getColumnValueByIndex (int startIndex, int endIndex, String attribute) {
    List<MatrixAnnotation<?>> result = new ArrayList<> ();

    final Column column = dbDataContext.getDefaultSchema ()
                                       .getTableByName (currentTableName)
                                       .getColumnByName (attribute);
    final Column mevIndexColumn = dbDataContext.getDefaultSchema ()
                                               .getTableByName (currentTableName)
                                               .getColumnByName (INDEX_COL_NAME);
    boolean isQuantitative = column.getType ().isNumber ();
    Set<Object> categorical = new HashSet<> ();
    FilterItem filter = new FilterItem (LogicalOperator.AND,
                                        new FilterItem (new SelectItem (mevIndexColumn),
                                                        OperatorType.GREATER_THAN,
                                                        startIndex - 1),
                                        new FilterItem (new SelectItem (mevIndexColumn),
                                                        OperatorType.LESS_THAN,
                                                        endIndex + 1));

    Query q = dbDataContext.query ().from (this.currentTableName).select (attribute)
                           .where (filter)
                           .toQuery ();

    log.debug ("Query: " + q.toString ());
    try (DataSet ds = dbDataContext.executeQuery (q)) {
      Number min = Double.MAX_VALUE;
      Number max = Double.MIN_VALUE;

      // some data found

      if (ds.next ()) {
        if (isQuantitative) {
          // Numeric
          Query qMinMax = dbDataContext.query ().from (this.currentTableName)
                                       .select (FunctionType.MIN, column)
                                       .select (FunctionType.MAX, column)
                                       .where (filter)
                                       .toQuery ();
          log.debug ("Quantitative query result: " + qMinMax);
          try (DataSet dsMinMax = dbDataContext.executeQuery (qMinMax)) {
            if (dsMinMax.next ()) {
              Row row = dsMinMax.getRow ();
              min = (Number) row.getValue (0);
              max = (Number) row.getValue (1);
            }
          }
        } else {
          // Categorical
          Query qCategorical = dbDataContext.query ().from (this.currentTableName)
                                            .select (column)
                                            .where (filter)
                                            .groupBy (column)
                                            .toQuery ();
          log.debug ("Categorical query result: " + qCategorical);
          try (DataSet dsCategories = dbDataContext.executeQuery (qCategorical)) {
            while (dsCategories.next ()) {
              Row row = dsCategories.getRow ();
              categorical.add (row.getValue (0));
            }
          }
        }
      }

      do {
        Row row = ds.getRow ();
        String value = (String) row.getValue (0);

        result.add (new MatrixAnnotation<Object> (attribute,
                                                  value,
                                                  isQuantitative ? QUANTITATIVE : CATEGORICAL,
                                                  isQuantitative ? asList (min, max) : categorical));
      } while (ds.next ());
    }
    return result;
  }

  private boolean importTable (UpdateableDataContext targetDataContext,
                               Table targetTable,
                               DataContext sourceDataContext,
                               Table sourceTable) {
    // if the table already exists, don't override it
    // import into a new table, and later copy the indexes
    boolean isNew = targetTable == null;
    log.debug ("Importing " + (isNew ? "new " : "") + "source " + sourceTable + " with context " + sourceDataContext + " to target " + targetTable + " with context " + targetDataContext);
    boolean ret = false;

    String targetTableName;
    if (isNew)
      targetTableName = this.dataNamespace;
    else
      targetTableName = generateUniqueTableName ();

    // create a new table for the data
    Table newTable = createTable (targetDataContext, sourceDataContext, sourceTable, targetTableName);

    // import data:
    // if new - just write the data to the table
    // if update - write the data to a new table,
    // - then import mev_index from old table\
    // - then drop the old table
    if (isNew) {
      ret = insertTableData (targetDataContext, newTable, sourceDataContext, sourceTable);
    }
    else {
      if (updateTableData (targetDataContext, newTable, sourceDataContext, sourceTable, targetTable))
        ;
      {
        dropTable (targetDataContext, this.currentTableName);
      }
    }
    this.currentTableName = targetTableName;

    return ret;
  }

  private Table createTable (final UpdateableDataContext targetDataContext,
                             final DataContext sourceDataContext,
                             final Table theSourceTable,
                             final String targetTableName) {
    @Accessors (fluent = true, chain = true)
    @Data
    class TableHolder {
      private Table table;
    }

    final TableHolder holder = new TableHolder ();
    targetDataContext.executeUpdate (new BatchUpdateScript () {

      @Override
      public void run (UpdateCallback callback) {
        // get the target data context
        DataContext dc = callback.getDataContext ();

        // create schema
        TableCreationBuilder tcb = callback.createTable (targetDataContext.getDefaultSchema (), targetTableName);
        ColumnCreationBuilder mevIndexCB = null;
        for (Column column : theSourceTable.getColumns ()) {
          String columnName = column.getName ();
          // assume the first column in the annotations is the unique identifier
          // if it's name is blank, assign a default "annotationId"
          if (column.getColumnNumber () == 0 && column.getName ().equals (""))
            columnName = "annotationId";
          // create the column def
          log.debug (columnName + " (" + column.getType () + "|" + column.getNativeType () + ")");
          ColumnCreationBuilder ccb = tcb.withColumn (columnName)
                                         .ofNativeType (column.getNativeType ())
                                         .nullable (true);
          // remember if mev_index column is supplied in the dataset
          if (column.getName ().equals (INDEX_COL_NAME))
            mevIndexCB = ccb;
          // if not mev_index, all columns are imported as varchar(255)
          else if (column.getType () == ColumnType.VARCHAR)
            ccb.ofSize (255);
        }

        if (mevIndexCB == null)
          mevIndexCB = tcb.withColumn (INDEX_COL_NAME);
        mevIndexCB.ofType (ColumnType.INTEGER).asPrimaryKey ();
        holder.table (tcb.execute ());

        // mev_index must be an integer
        Column mevIndexColumn = holder.table ().getColumnByName (INDEX_COL_NAME);
        dc = Converters.addTypeConverter (dc, mevIndexColumn, new StringToIntegerConverter ());
      }
    });

    return holder.table ();
  }

  private boolean dropTable (final UpdateableDataContext targetDataContext, final String targetTableName) {
    targetDataContext.executeUpdate (new BatchUpdateScript () {

      @Override
      public void run (UpdateCallback callback) {
        callback.dropTable (targetDataContext.getDefaultSchema (), targetTableName).execute ();
      }
    });
    return true;
  }

  private boolean insertTableData (final UpdateableDataContext targetDataContext,
                                   final Table targetTable,
                                   final DataContext sourceDataContext,
                                   final Table sourceTable) {
    targetDataContext.executeUpdate (new BatchUpdateScript () {

      @Override
      public void run (UpdateCallback callback) {
        // get the target data context
        DataContext dc = callback.getDataContext ();

        // mev_index must be an integer
        Column mevIndexColumn = targetTable.getColumnByName (INDEX_COL_NAME);
        dc = Converters.addTypeConverter (dc, mevIndexColumn, new StringToIntegerConverter ());

        // import data
        Query sourceQuery = sourceDataContext.query ().from (sourceTable).selectAll ().toQuery ();
        try (DataSet ds = sourceDataContext.executeQuery (sourceQuery)) {
          boolean isMevIndexProvided = (sourceTable.getColumnByName (INDEX_COL_NAME) != null);
          int mev_index_counter = 0;
          while (ds.next ()) {
            Row row = ds.getRow ();
            RowInsertionBuilder rib = callback.insertInto (targetTable);
            // iterate through columns
            StringBuffer debug = new StringBuffer ();
            for (SelectItem si : row.getSelectItems ()) {
              if (log.isDebugEnabled ())
                debug.append (row.getValue (si) + "\t");
              rib = rib.value (si.getColumn ().getName (), row.getValue (si));
            }
            if (!isMevIndexProvided) {
              if (log.isDebugEnabled ())
                debug.append (mev_index_counter + "\t");
              rib = rib.value (INDEX_COL_NAME, mev_index_counter++);
            }

            log.debug (debug + rib.toSql ());
            rib.execute ();
          }
        }
      }
    });
    return true;
  }

  private boolean updateTableData (final UpdateableDataContext targetDataContext,
                                   final Table targetTable,
                                   final DataContext sourceDataContext,
                                   final Table sourceTable,
                                   final Table indexLookupTable) {
    targetDataContext.executeUpdate (new BatchUpdateScript () {

      @Override
      public void run (UpdateCallback callback) {
        // get the target data context
        DataContext dc = callback.getDataContext ();

        // mev_index must be an integer
        Column mevIndexColumn = targetTable.getColumnByName (INDEX_COL_NAME);
        dc = Converters.addTypeConverter (dc, mevIndexColumn, new StringToIntegerConverter ());

        // import data
        Query sourceQuery = sourceDataContext.query ().from (sourceTable).selectAll ().toQuery ();
        try (DataSet sourceDS = sourceDataContext.executeQuery (sourceQuery)) {
          String indexLookupColumnName = indexLookupTable.getColumn (0).getName ();
          boolean isError = false;
          while (sourceDS.next ()) {

            Row row = sourceDS.getRow ();
            RowInsertionBuilder rib = callback.insertInto (targetTable);
            // iterate through columns
            for (SelectItem si : row.getSelectItems ()) {
              log.debug (row.getValue (si) + "\t");
              // the first column is the annotationId
              if (si.getColumn ().getColumnNumber () == 0) {
                Query lookupAnnIndexQuery = targetDataContext.query ()
                                                             .from (indexLookupTable)
                                                             .select (INDEX_COL_NAME)
                                                             .where (indexLookupColumnName)
                                                             .eq (row.getValue (si)).toQuery ();
                log.debug (lookupAnnIndexQuery.toSql ());
                try (DataSet indexDS = targetDataContext.executeQuery (lookupAnnIndexQuery)) {
                  if (!indexDS.next ()) {
                    log.debug ("AnnotationId '"
                               + row.getValue (si) + "' was not found in "
                               + indexLookupTable.getQualifiedLabel ());
                    isError = true;
                    break;
                  }
                  else {
                    rib = rib.value (INDEX_COL_NAME, indexDS.getRow ().getValue (0));
                    rib = rib.value (targetTable.getColumn (0).getName (), row.getValue (si));
                  }
                }
              }
              else {
                if (!si.getColumn ().getName ().equals (INDEX_COL_NAME))
                  rib = rib.value (si.getColumn ().getName (), row.getValue (si));
              }
            }
            log.debug (rib.toSql ());
            if (!isError)
              rib.execute ();
          }
        }
      }
    });
    return true;
  }

  private void closeCleanup () {
    dbDataContext.executeUpdate (new BatchUpdateScript () {

      @Override
      public void run (UpdateCallback callback) {
        DataContext dc = callback.getDataContext ();

        for (String tableName : dc.getDefaultSchema ().getTableNames ()) {
          if (tableName.startsWith (dataNamespace, 0)) {
            log.debug ("Dropping table " + tableName + " on close");
            callback.dropTable (dc.getDefaultSchema (), tableName).execute ();
          }
        }
      }
    });
  }

  private Table getTableByName (DataContext dataContext, String tableName) {
    Table table = dataContext.getTableByQualifiedLabel (tableName);
    if (table == null)
      log.debug ("Table " + tableName + "=" + table);
    return table;
  }

  @SuppressWarnings ("unused")
  private void query (String attribute, String value) {
    Schema schema = dbDataContext.getDefaultSchema ();
    Table[] tables = schema.getTables ();

    // CSV files only has a single table in the default schema
    assert tables.length == 1;
    Table table = tables[0];

    // there are several ways to get columns - here we simply get them by name
    // Column firstNameColumn = table.getColumnByName("first_name");
    // Column lastNameColumn = table.getColumnByName("last_name");

    // use the table and column types in the query
    Query q = dbDataContext.query ()
                           .from (table)
                           .select (attribute)
                           .where (table.getColumns ()[0].getName ())
                           .eq (value)
                           .toQuery ();
    log.debug (q.toString ());

    DataSet ds = dbDataContext.executeQuery (q);

    while (ds.next ()) {
      Row row = ds.getRow ();
      String val = (String) row.getValue (0);
      log.debug (attribute + ": '" + val + "'");
    }
    ds.close ();
  }

  @SuppressWarnings ("unused")
  private void showSchema (DataContext dataContext) {
    Schema[] schemas = dataContext.getSchemas ();

    // iterate through schemas
    for (Schema schema : schemas) {

      log.debug (schema.getName ());
      Table[] tables = schema.getTables ();

      // iterate through tables
      for (Table table : tables) {

        log.debug ("  " + table.getName () + " (" + table.getType () + ")");
        Column[] columns = table.getColumns ();

        // iterate through columns
        for (Column column : columns) {

          log.debug ("    "
                     + column.getName () + " (" + column.getType () + "|" + column.getNativeType () + ")");
        }
      }
    }
  }

  @SuppressWarnings ("unused")
  private void showAllQueryData (DataContext dataContext, Query q) {
    log.debug (q.toString ());

    try (DataSet ds = dataContext.executeQuery (q)) {

      // iterate through columns
      StringBuffer debug = new StringBuffer ();
      for (SelectItem si : ds.getSelectItems ()) {
        Column column = si.getColumn ();
        debug.append (column.getName () + "\t");
      }
      log.debug (debug.toString ());

      while (ds.next ()) {
        Row row = ds.getRow ();
        // iterate through columns
        debug = new StringBuffer ();
        for (SelectItem si : row.getSelectItems ()) {
          debug.append (row.getValue (si) + "\t");
        }
        log.debug (debug.toString ());
      }
    }
  }

  public void getAllData () {
    Schema[] schemas = dbDataContext.getSchemas ();

    // iterate through schemas
    for (Schema schema : schemas) {

      log.debug (schema.getName ());
      // CSV files only has a single table in the default schema
      Table[] tables = schema.getTables ();
      assert tables.length == 1;
      Table table = tables[0];

      log.debug ("  " + table.getName () + " (" + table.getType () + ")");

      // use the table and column types in the query
      Query q = dbDataContext.query ().from (table).selectAll ().toQuery ();
      log.debug (q.toString ());

      DataSet ds = dbDataContext.executeQuery (q);

      // iterate through columns
      StringBuffer debug = new StringBuffer ();
      for (SelectItem si : ds.getSelectItems ()) {
        Column column = si.getColumn ();
        debug.append (column.getName () + "\t");
      }
      log.debug (debug);

      while (ds.next ()) {
        Row row = ds.getRow ();
        // iterate through columns
        debug = new StringBuffer ();
        for (SelectItem si : row.getSelectItems ()) {
          debug.append (row.getValue (si) + "\t");
        }
        log.debug (debug.toString ());
      }
      ds.close ();
    }
  }

  @Override
  public void close () throws IOException {
    closeCleanup ();
  }
}
