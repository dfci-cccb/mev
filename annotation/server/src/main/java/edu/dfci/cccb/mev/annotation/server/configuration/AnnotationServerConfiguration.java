package edu.dfci.cccb.mev.annotation.server.configuration;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.ToString;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.refine.ProjectManager;
import com.google.refine.SessionWorkspaceDir;
import com.google.refine.io.FileProjectManager;

import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;

@Configuration
@ComponentScan (value = "edu.dfci.cccb.mev.annotation",
                includeFilters = @Filter (type = ANNOTATION, value = {
                                                                      Controller.class,
                                                                      ControllerAdvice.class,
                                                                      RestController.class }))
@ToString
public class AnnotationServerConfiguration extends WebMvcConfigurerAdapter {

  private @Inject Workspace workspace;
  private @Inject FileProjectManager sessionProjectManager;
  private @Inject DatasetBuilder builder;
  

  @Bean
  @Scope (value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public FileProjectManager sessionProjectManager () {
    FileProjectManager projectManager = new FileProjectManager ();
    projectManager.setWorkspaceDir (new SessionWorkspaceDir ());
    projectManager.setWorkspace (workspace);
    projectManager.setDatasetBuilder (builder);
    return projectManager;
  }


  @PostConstruct
  public void setProjectmanagerSingleton () {
    ProjectManager.setSingleton (sessionProjectManager);
  }
}
