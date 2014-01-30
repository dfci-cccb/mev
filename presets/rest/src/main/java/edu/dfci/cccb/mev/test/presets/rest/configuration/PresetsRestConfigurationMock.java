package edu.dfci.cccb.mev.test.presets.rest.configuration;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

import java.io.IOException;
import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import edu.dfci.cccb.mev.presets.rest.configuration.PresetsRestConfiguration;

@Configuration
@Import (PresetsRestConfiguration.class)
public class PresetsRestConfigurationMock extends WebMvcConfigurerAdapter{

  @Bean (name="tcgaPresetRoot")
  public URL tcgaPresetRoot() throws IOException{    
    return (new ClassPathResource ("tcga/")).getURL ();
  }

}
