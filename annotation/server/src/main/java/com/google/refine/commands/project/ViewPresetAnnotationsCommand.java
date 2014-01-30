package com.google.refine.commands.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.ProjectManager;
import com.google.refine.ProjectMetadata;
import com.google.refine.commands.Command;
import com.google.refine.model.Project;
import com.google.refine.util.ParsingUtilities;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetDescriptor;
import freemarker.template.utility.NullArgumentException;

public class ViewPresetAnnotationsCommand extends Command {

  final static protected Logger logger = LoggerFactory.getLogger("ViewPresetAnnotationsCommand");
  
  @Override
  public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // TODO Auto-generated method stub
    ProjectManager.getSingleton().setBusy(true);
    try {
        Properties options = ParsingUtilities.parseUrlParameters(request);
        if(!options.containsKey ("import-preset"))
          throw new NullArgumentException ("import-preset");
        
        String datasetName = options.getProperty ("import-preset");
        long projectID = Project.generateID();
        logger.info("Importing existing project using new ID {}", projectID);
        //File file = new File("/tmp/textxxx/presets/"+datasetName+"/"+datasetName+".annotations.gz");
        PresetDescriptor descriptor = (PresetDescriptor)request.getAttribute ("descriptor");
        File file = new File( descriptor.columnUrl ().getFile() );
        
        String fileName = file.getName().toLowerCase();
        InputStream stream = new FileInputStream (file);
        
        ProjectManager.getSingleton().importProject(projectID, stream, !fileName.endsWith(".tar"));
        ProjectManager.getSingleton().loadProjectMetadata(projectID);

        ProjectMetadata pm = ProjectManager.getSingleton().getProjectMetadata(projectID);
        if (pm != null) {
            if (options.containsKey("project-name")) {
                String projectName = options.getProperty("project-name");
                if (projectName != null && projectName.length() > 0) {
                    pm.setName(projectName);
                }
            }
            redirect(response, "/annotations/import-dataset/project?"
                     +"import-preset="+datasetName+"&project=" + projectID);
        } else {
            respondWithErrorPage(request, response, "Failed to import project. Reason unknown.", null);
        }
    } catch (Exception e) {
        respondWithErrorPage(request, response, "Failed to import project", e);
    } finally {
        ProjectManager.getSingleton().setBusy(false);
    }
  }
  
}
