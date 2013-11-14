package edu.dfci.cccb.mev.annotation.server.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.ModelAndView;

import com.google.refine.RefineServlet;

import edu.dfci.cccb.mev.heatmap.domain.Heatmap;
import edu.dfci.cccb.mev.heatmap.domain.HeatmapNotFoundException;
import edu.dfci.cccb.mev.heatmap.domain.Workspace;
import edu.dfci.cccb.mev.test.mock.MockHeatmap;

@Controller
@RequestMapping ("/annotations")
@Log4j
public class AnnotationController extends WebApplicationObjectSupport {

  private RefineServlet refineServlet;
  private @Inject Environment environment;
  private @Inject Workspace workspace;

  @PostConstruct
  private void createRefineServlet () throws ServletException {
    refineServlet = new RefineServlet ();
    refineServlet.init (new DelegatingServletConfig ());
  }

  @RequestMapping ("/")
  public ModelAndView annotationsHome () {

    try {
      Heatmap heatmap = workspace.get ("mock");
    } catch (HeatmapNotFoundException e) {
      workspace.put (new MockHeatmap ("mock"));
    }

    ModelAndView mav = new ModelAndView ();
    mav.addObject ("heatmaps", workspace.list ());
    mav.setViewName ("annotations");
    return mav;

  }

  @RequestMapping (method = { GET, POST, PUT, DELETE }, value = { "/{heatmapId}/annotation/{dimension}/**" })
  @ResponseBody
  public void handleAnnotation (@PathVariable ("heatmapId") final String heatmapId,
                                @PathVariable ("dimension") final String dimension,
                                HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                         IOException {
    log.debug (String.format ("Handling annotation request: %s", request.getServletPath ()));
    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper (request) {
      @Override
      public String getPathInfo () {
        return super.getServletPath ().replace ("/annotations/" + heatmapId + "/annotation/" + dimension, "");
      }
    };

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
