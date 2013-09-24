package edu.dfci.cccb.mev.domain;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import edu.dfci.cccb.mev.support.AnnotationDataAccessLayer;

public class Annotations implements Closeable {

  private final AnnotationDimension dim;
  private final AnnotationDataAccessLayer dal;

  public Annotations (UUID universalId, AnnotationDimension dim, DataSource dataSource) {
    this.dim = dim;
    this.dal = new AnnotationDataAccessLayer (dataSource, universalId.toString () + "-" + this.dim.toString ());
  }

  public boolean setAnnotations (InputStream data) {
    dal.setAnnotations (data);
    return true;
  }

  public boolean setAnnotations (List<Map<String, ?>> data) {
    dal.setAnnotations (data);
    return true;
  }

  public Collection<String> getAttributes () {
    return dal.getColumnNames ();
  }

  public List<MatrixAnnotation<?>> getByIndex (int startIndex,
                                               int endIndex,
                                               String attribute) throws AnnotationNotFoundException {
    if (!dal.getColumnNames ().contains (attribute))
      throw new AnnotationNotFoundException (attribute);

    List<MatrixAnnotation<?>> result = new ArrayList<> ();

    result = dal.getColumnValueByIndex (startIndex, endIndex, attribute);
    return result;
  }

  @Override
  public void close () throws IOException {
    dal.close ();
  }
}

// ================= Examples =======================
// CREATING A TABLE
/* Table table = callback.createTable(schema, importTable.getName() )
 * .withColumn("name").ofType(ColumnType.VARCHAR).nullable(true).ofSize(45)
 * .withColumn("gender").ofType(ColumnType.VARCHAR).nullable(true).ofSize(45)
 * .withColumn("age").ofType(ColumnType.INTEGER).nullable(true) .execute();
 * callback
 * .insertInto(table).value("name","John Doe").value("gender",'M').value(
 * "age",42).execute();
 * callback.insertInto(table).value("name","Jane Doe").value
 * ("gender",'F').value("age",43).execute(); */

// ==================== Hibernate Entity Code ===========================
// DataSource dataSource = annotationSessionFactory.getDataSource();
/* Session s = sessionFactory.getCurrentSession(); Course course = new Course();
 * course.setCourseId("IDA512"); s.saveOrUpdate(course); s.flush(); s.clear();
 * Course course2 = (Course) s.get(Course.class, "IDA511"); s.close(); */