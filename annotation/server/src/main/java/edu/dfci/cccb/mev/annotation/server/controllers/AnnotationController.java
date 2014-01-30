package edu.dfci.cccb.mev.annotation.server.controllers;

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.COLUMN;
import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.ROW;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DatasetPathVariableMethodArgumentResolver.DATASET_MAPPING_NAME;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DatasetPathVariableMethodArgumentResolver.DATASET_URL_ELEMENT;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DimensionPathVariableMethodArgumentResolver.DIMENSION_MAPPING_NAME;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.DimensionPathVariableMethodArgumentResolver.DIMENSION_URL_ELEMENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.ModelAndView;

import com.google.refine.RefineServlet;
import com.google.refine.io.FileProjectManager;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetNotFoundException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;
import edu.dfci.cccb.mev.dataset.domain.mock.MockTsvInput;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListSelections;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDataset;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDimension;
import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.Presets;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetNotFoundException;
import freemarker.template.TemplateModelException;

@Controller
@RequestMapping ("/annotations")
@Log4j
public class AnnotationController extends WebApplicationObjectSupport {

  private RefineServlet refineServlet;  
  private @Inject Environment environment;
  private @Inject Workspace workspace;
  private @Inject FileProjectManager projectManager;
  private @Inject DatasetBuilder datasetBuilder;
  private @Inject Presets presets;
  
  @PostConstruct
  private void createRefineServlet () throws ServletException {
    refineServlet = new RefineServlet ();
    refineServlet.init (new DelegatingServletConfig ());
  }

  @RequestMapping ("/")
  public ModelAndView annotationsHome () throws InvalidDatasetNameException,
                                        TemplateModelException,
                                        DatasetBuilderException,
                                        InvalidDimensionTypeException {

    try {
      workspace.get ("mock");
    } catch (DatasetNotFoundException e) {
      Dimension columns = new SimpleDimension (COLUMN,
                                               new ArrayList<String> (Arrays.asList ("a", "b", "c")),
                                               new ArrayListSelections (),
                                               null);
      Dimension rows = new SimpleDimension (ROW,
                                            new ArrayList<String> (Arrays.asList ("aaa", "bbb", "ccc")),
                                            new ArrayListSelections (),
                                            null);
      Dataset mockHeatmap = new SimpleDataset ("mock", null, null, columns, rows);

      workspace.put (mockHeatmap);
    }

    try {
      workspace.get ("build-mock");
    } catch (DatasetNotFoundException e) {
      Dataset set = datasetBuilder.build (new MockTsvInput ("build-mock", "id\tsa\tsb\tsc\n" +
                                                                          "g1\t.1\t.2\t.3\n" +
                                                                          "g2\t.4\t.5\t.6"));
      workspace.put (set);
    }

    try {
      workspace.get ("shmock");
    } catch (DatasetNotFoundException e) {
      Dimension columns =
                          new SimpleDimension (COLUMN,
                                               new ArrayList<String> (Arrays.asList ("e", "f", "g")),
                                               new ArrayListSelections (),
                                               null);
      Dimension rows =
                       new SimpleDimension (ROW,
                                            new ArrayList<String> (Arrays.asList ("eee", "fff", "ggg")),
                                            new ArrayListSelections (),
                                            null);
      Dataset mockHeatmap = new SimpleDataset ("shmock", null, null, columns, rows);

      workspace.put (mockHeatmap);
    }

    ModelAndView mav = new ModelAndView ();

    /* TemplateHashModel enumModels =
     * BeansWrapper.getDefaultInstance().getEnumModels(); TemplateHashModel
     * roundingModeEnums = (TemplateHashModel)
     * enumModels.get("java.math.RoundingMode"); TemplateHashModel
     * dimensionTypeEnums = (TemplateHashModel)
     * enumModels.get("edu.dfci.cccb.mev.dataset.domain.contract.Dimension$Type"
     * ); mav.addObject("RoundingMode", roundingModeEnums);
     * mav.addObject("Dimension.Type", dimensionTypeEnums);
     * mav.addObject("DimensionType", dimensionTypeEnums);
     * mav.addObject("Dimension$Type", dimensionTypeEnums);
     * mav.addObject("enums",
     * BeansWrapper.getDefaultInstance().getEnumModels()); Type type =
     * Type.COLUMN; Type type2 = Type.valueOf ("COLUMN"); mav.addObject ("type",
     * type); mav.addObject ("type2", type2); */

    mav.addObject ("workspace", workspace);
    mav.setViewName ("annotations");
    return mav;

  }

  @RequestMapping(method={GET, POST, PUT, DELETE}, value="/openrefine/**")
  public void openRefine(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    
    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper (request) {
      @Override
      public String getPathInfo () {
        return super.getServletPath ().replace ("/annotations/openrefine", "");
      }
    };
    
    this.refineServlet.service (wrappedRequest, response);
  }
  
  @RequestMapping(method={GET, POST, PUT, DELETE}, value="/import-dataset/**")
  public void importDataset(@RequestParam(value="import-preset", required=false) String presetName,
                            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, PresetNotFoundException{
    
    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper (request) {
      @Override
      public String getPathInfo () {
        return super.getServletPath ().replace ("/annotations/import-dataset", "");
      }
    };
    
    if(presetName!=null && presetName.equals ("")==false){
      Preset preset = presets.get (presetName);
      wrappedRequest.setAttribute ("descriptor", preset.descriptor ());
    }
    this.refineServlet.service (wrappedRequest, response);
  }
  
  @RequestMapping(method={GET, POST, PUT, DELETE}, value = {"/"
          + DATASET_URL_ELEMENT + "/annotation/"
          + DIMENSION_URL_ELEMENT + "/{selectionName}/**"

  })
  @ResponseBody
  public void handleAnnotationByName (@PathVariable (DATASET_MAPPING_NAME) final String heatmapId,
                                @PathVariable (DIMENSION_MAPPING_NAME) final String dimension,
                                @PathVariable (value="selectionName") final String selectionName,
                                HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                         IOException,
                                                                                         DatasetNotFoundException {
    log.debug (String.format ("Handling annotation request: %s", request.getServletPath ()));
    
    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper (request) {
      @Override
      public String getPathInfo () {
        return super.getServletPath ().replace ("/annotations/" + heatmapId + "/annotation/" + dimension + "/" + selectionName, "");
      }
    };

    Dataset heatmap = workspace.get (heatmapId);
    long projectId = projectManager.getProjectID (heatmap.name ()+dimension);
    if (projectId != -1) {
      if (wrappedRequest.getPathInfo ().trim ().equals ("/")) {
        if (wrappedRequest.getParameter ("reset") != null) {
          projectManager.deleteProject (projectId);
        } else {
          response.sendRedirect ("project?project=" + projectId);
          return;
        }
      }
    }

    wrappedRequest.setAttribute ("dataset", heatmap);
    wrappedRequest.setAttribute ("dimension", dimension);    
    if(!selectionName.equalsIgnoreCase ("new"))
      wrappedRequest.setAttribute ("selectionName", selectionName);
    this.refineServlet.service (wrappedRequest, response);    
  }
  /**
   * Internal implementation of the ServletConfig interface, to be passed to the
   * wrapped servlet. Delegates to ServletWrappingController fields and methods
   * to provide init parameters and other environment info.
   */
  private class DelegatingServletConfig implements ServletConfig {

    private Properties properties;

    {
      properties = new Properties ();
      properties.setProperty ("refine.version",
                              environment.getProperty ("refine.version", "$VERSION"));
      properties.setProperty ("refine.revision", environment.getProperty ("refine.revision", "$REVISION"));
      properties.setProperty ("refine.data",
                              environment.getProperty ("refine.data", System.getProperty ("java.io.tmpdir")));
    }

    public String getServletName () {
      return "refine";
    }

    public ServletContext getServletContext () {
      return AnnotationController.this.getServletContext ();
    }

    public String getInitParameter (String paramName) {
      return properties.getProperty (paramName);
    }

    @SuppressWarnings ({ "unchecked", "rawtypes" })
    public Enumeration getInitParameterNames () {
      return properties.keys ();
    }
  }
}
