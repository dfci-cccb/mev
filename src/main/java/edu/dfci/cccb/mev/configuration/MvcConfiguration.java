/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.util.List;
import java.util.Locale;

import lombok.experimental.ExtensionMethod;

import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import us.levk.spring.web.log4javascript.controllers.Log4JavascriptController;
import us.levk.spring.web.method.CookiesHandlerArgumentResolver;
import edu.dfci.cccb.mev.domain.Heatmap;

/**
 * @author levk
 * 
 */
@Configuration
@EnableWebMvc
@ComponentScan ("edu.dfci.cccb.mev")
@PropertySource ({
                  "classpath:/configuration/default-client-configuration.properties",
                  "classpath:/configuration/client-logging.properties",
                  "classpath:/configuration/profile.properties" })
@ExtensionMethod (Level.class)
public class MvcConfiguration extends WebMvcConfigurerAdapter {

  private @Autowired Environment environment;

  /**
   * Loads build properties as an application accessible bean
   * 
   * @return
   */
  @Bean (name = "buildProperties")
  public PropertiesFactoryBean globalBuildProperties () {
    return new PropertiesFactoryBean () {
      {
        setLocation (new ClassPathResource ("build.properties"));
      }
    };
  }

  @Bean (name = "clientLoggingProperties")
  public PropertiesFactoryBean clientLoggingProperties () {
    return new PropertiesFactoryBean () {
      {
        setLocation (new ClassPathResource ("configuration/client-logging.properties"));
      }
    };
  }

  @Bean (name = "profileProperties")
  public PropertiesFactoryBean profileProperties () {
    return new PropertiesFactoryBean () {
      {
        setLocation (new ClassPathResource ("configuration/profile.properties"));
      }
    };
  }

  @Bean (name = "messageSource")
  public ReloadableResourceBundleMessageSource messageSource () {
    return new ReloadableResourceBundleMessageSource () {
      {
        setBasename ("classpath:i18n/home");
        setDefaultEncoding ("UTF-8");
      }
    };
  }

  /**
   * Create the CNVR. Get Spring to inject the ContentNegotiationManager created
   * by the configurer (see previous method).
   */
  @Bean (name = "contentNegotiatingViewResolver ")
  public ViewResolver contentNegotiatingViewResolver (final ContentNegotiationManager manager) {
    return new ContentNegotiatingViewResolver () {
      {
        setContentNegotiationManager (manager);
      }
    };
  }

  /**
   * Multipart resolver for file upload
   * 
   * @return
   */
  @Bean (name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver () {
    return new CommonsMultipartResolver ();
  }

  /**
   * JSP view resolver
   * 
   * @return
   */
  @Bean (name = "jspViewResolver")
  public ViewResolver jspViewResolver () {
    return new InternalResourceViewResolver () {
      {
        setPrefix ("/META-INF/resources/mev/views/");
        setSuffix (".jsp");
        setOrder (2);
        setExposeContextBeansAsAttributes (true);
      }
    };
  }

  /**
   * JSON view resolver
   * 
   * @return
   */
  @Bean (name = "jsonViewResolver")
  public ViewResolver jsonViewResolver () {
    return new ViewResolver () {

      @Override
      public View resolveViewName (String viewName, Locale locale) throws Exception {
        return new MappingJackson2JsonView () {
          {
            setPrettyPrint (true);
          }
        };
      }
    };
  }

  /**
   * Simple URL handler mapping bean used for utility controllers
   * 
   * @return
   */
  @Bean (name = "simpleUrlHandlerMapping")
  public SimpleUrlHandlerMapping simpleUrlHandlerMapping () {
    return new SimpleUrlHandlerMapping () {
      {
        // log4javascript
        Log4JavascriptController controller;
        controller = new Log4JavascriptController (environment.getProperty ("log4javascript.mapping"));
        controller.setRootVariableName (environment.getProperty ("log4javascript.root.log.variable"));
        controller.setRootLevel (environment.getProperty ("log4javascript.root.log.level").toLevel ());
        if (environment.getProperty ("log4javascript.console.enabled", Boolean.class))
          controller.enableConsoleAppender (environment.getProperty ("log4javascript.console.pattern"),
                                            environment.getProperty ("log4javascript.console.threshold").toLevel ());
        else
          controller.disableConsoleAppender ();
        if (environment.getProperty ("log4javascript.logback.enabled", Boolean.class))
          controller.enableLogbackAppender (environment.getProperty ("log4javascript.logback.threshold").toLevel ());
        else
          controller.disableLogbackAppender ();
        if (environment.getProperty ("log4javascript.popup.enabled", Boolean.class))
          controller.enablePopupAppender (environment.getProperty ("log4javascript.popup.pattern"),
                                          environment.getProperty ("log4javascript.popup.threshold").toLevel ());
        else
          controller.disablePopupAppender ();
        if (environment.getProperty ("log4javascript.alert.enabled", Boolean.class))
          controller.enableAlertAppender (environment.getProperty ("log4javascript.alert.threshold").toLevel ());
        else
          controller.disableAlertAppender ();
        if (environment.getProperty ("log4javascript.inpage.enabled", Boolean.class))
          controller.enableInpageAppender (environment.getProperty ("log4javascript.inpage.pattern"),
                                           environment.getProperty ("log4javascript.inpage.threshold").toLevel ());
        else
          controller.disableInpageAppender ();
        controller.configure (this);
      }
    };
  }

  /**
   * Heatmap container bean
   * 
   * @return
   */
  // TODO: Move this off to a separate heatmap config
  @Bean (name = "heatmapBuilder")
  public Heatmap.Builder heatmapBuilder () {
    return new Heatmap.Builder ().allowComments (false).allowEmptyLines (false).annotationProcessor (null);
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
   * #configureContentNegotiation
   * (org.springframework.web.servlet.config.annotation
   * .ContentNegotiationConfigurer) */
  @Override
  public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension (true)
              .favorParameter (true)
              .parameterName ("format")
              .ignoreAcceptHeader (true)
              .useJaf (false)
              .defaultContentType (TEXT_HTML)
              .mediaType ("html", TEXT_HTML)
              .mediaType ("xml", APPLICATION_XML)
              .mediaType ("tsv", TEXT_PLAIN)
              .mediaType ("json", APPLICATION_JSON);
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
   * #addResourceHandlers
   * (org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry) */
  @Override
  public void addResourceHandlers (ResourceHandlerRegistry registry) {
    registry.addResourceHandler ("/resources/static/**")
            .addResourceLocations ("classpath:/META-INF/resources/", "/META-INF/resources/");
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
   * #addArgumentResolvers(java.util.List) */
  @Override
  public void addArgumentResolvers (List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add (new CookiesHandlerArgumentResolver ());
  }
}
