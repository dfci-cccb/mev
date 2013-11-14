package edu.dfci.cccb.mev.annotation.server.configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import edu.dfci.cccb.mev.api.client.support.injectors.InjectorRegistry;
import edu.dfci.cccb.mev.api.client.support.injectors.ViewRegistrar;

@Configuration
@Log4j
public class AnnotationClientConfiguration extends WebMvcConfigurerAdapter {

  private @Inject ViewRegistrar views;
  private @Inject InjectorRegistry injectors;
  
  @PostConstruct
  public void registerViews () {
    log.debug ("Registering views in " + Views.class.getName () + " with " + views);
    views.registerAnnotatedViewBeanClasses (Views.class);
  }

  @PostConstruct
  public void registerInjectors () {
    injectors.add ("/annotation/javascript/main.js");
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
   * #addResourceHandlers
   * (org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry) */
  @Override
  public void addResourceHandlers (ResourceHandlerRegistry registry) {
    log.debug ("Adding resource handlers for annotation plugin");
    registry.addResourceHandler ("/annotation/javascript/**")
            .addResourceLocations ("classpath:/edu/dfci/cccb/mev/annotation/client/javascript/");
    registry.addResourceHandler ("/annotation/style/**")
            .addResourceLocations ("classpath:/edu/dfci/cccb/mev/annotation/client/style/");
  }
}
