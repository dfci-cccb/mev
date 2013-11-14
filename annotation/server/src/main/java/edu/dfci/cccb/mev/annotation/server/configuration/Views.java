package edu.dfci.cccb.mev.annotation.server.configuration;

import static edu.dfci.cccb.mev.api.client.support.view.ViewBuilders.freemarker;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

public class Views {

	@Bean
	  public FreeMarkerView annotations () {
	    return freemarker ().url ("/edu/dfci/cccb/mev/annotation/client/views/annotations.ftl").build ();
	  }
}
