package com.google.refine.commands.project;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.ProjectManager;
import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.commands.Command;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.RawInput;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleSelection;
import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.UrlTsvInput;
import edu.dfci.cccb.mev.presets.contract.PresetDescriptor;

public class ImportPresetDatasetCommand extends Command {
  final static protected Logger logger = LoggerFactory.getLogger("ImportPresetDatasetCommand");
  private @Getter @Setter @Inject Workspace workspace;
  private @Getter @Setter @Inject DatasetBuilder builder;

  @Override
  public void doPost (final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    logger.info (String.format ("******************* Import Dataset: %s *******************",
                                request.getParameter ("import-preset")));
    ProjectManager.getSingleton ().setBusy (true);
    try {

      Project project = getProject (request);
      Engine engine = getEngine (request, project);      
      
      Dimension.Type dimensionType = Dimension.Type.COLUMN;
      final String sourceDatasetName = request.getParameter ("import-preset");
      final String newDatasetName = request.getParameter ("newDatasetName");
      final Properties properties = new Properties ();      
      final List<String> keys = new ArrayList<String> ();
      RowVisitor visitor = new RowVisitor () {
        int rowCount = 0;
        Column theIdColumn;

        @Override
        public void start (Project project) {

          // if no id column found, assume first column is the id
          List<Column> columns = project.columnModel.columns;
          theIdColumn = columns.get (0);

          for (Column column : columns) {
            String name = column.getName ();
            if (name.equalsIgnoreCase ("annotationId") || name.equalsIgnoreCase ("id")) {
              theIdColumn = column;
              break;
            }
          }
        }

        @Override
        public boolean visit (Project project, int rowIndex, Row row) {
          String cellData = row.getCell (theIdColumn.getCellIndex ()).value.toString ();
          if (cellData != null) {
            keys.add (cellData);
            rowCount++;
          }
          return false;
        }

        @Override
        public void end (Project project) {
          Selection sourceSelection = new SimpleSelection (newDatasetName, properties, keys);
          Dataset dataset=null;
          //File datafile = new File("/tmp/textxxx/presets/"+sourceDatasetName+"/"+sourceDatasetName+".tsv");
          PresetDescriptor descriptor = (PresetDescriptor)request.getAttribute ("descriptor");
          try {            
            RawInput newDatasetContent = new UrlTsvInput (descriptor.dataUrl ());            
            newDatasetContent.name (newDatasetName);
            logger.info (String.format ("***Import Dataset: %s *******************", descriptor.dataUrl ().toString ()));
            dataset = ProjectManager.getSingleton ().getDatasetBuilder ().build (newDatasetContent, sourceSelection);
            
          } catch (DatasetBuilderException | InvalidDatasetNameException | InvalidDimensionTypeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          ProjectManager.getSingleton ().getWorkspace ().put (dataset);
        }
      };

      FilteredRows filteredRows = engine.getAllFilteredRows ();
      filteredRows.accept (project, visitor);
      ProjectManager.getSingleton ().save (true);
      respond (response, "{ \"code\" : \"ok\" }");

    } catch (Exception e) {
      // Use generic error handling rather than our JSON handling
      // throw new ServletException(e);
      respondException (response, e);
    } finally {
      ProjectManager.getSingleton ().setBusy (false);
    }
  
  }
}
