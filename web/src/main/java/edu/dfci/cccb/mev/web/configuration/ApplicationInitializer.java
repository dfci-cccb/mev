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
package edu.dfci.cccb.mev.web.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import edu.dfci.cccb.mev.annotation.server.configuration.AnnotationServerConfiguration;
import edu.dfci.cccb.mev.dataset.rest.configuration.DatasetRestConfiguration;
import edu.dfci.cccb.mev.hcl.rest.configuration.HclRestConfiguration;
import edu.dfci.cccb.mev.limma.rest.configuration.LimmaRestConfiguration;
import edu.dfci.cccb.mev.presets.rest.configuration.PresetsRestConfiguration;
import edu.dfci.cccb.mev.web.configuration.container.ContainerConfigurations;

/**
 * @author levk
 * 
 */
public class ApplicationInitializer implements WebApplicationInitializer {

  /* (non-Javadoc)
   * @see
   * org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet
   * .ServletContext) */
  @Override
  public void onStartup (ServletContext servletContext) throws ServletException {
    AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext ();

    mvcContext.register (DispatcherConfiguration.class, PersistenceConfiguration.class, ContainerConfigurations.class);

    mvcContext.register (DatasetRestConfiguration.class);
    mvcContext.register (AnnotationServerConfiguration.class);
    mvcContext.register (HclRestConfiguration.class);
    mvcContext.register (LimmaRestConfiguration.class);
    mvcContext.register (PresetsRestConfiguration.class);

    DispatcherServlet dispatcher = new DispatcherServlet (mvcContext);
    Dynamic registration = servletContext.addServlet ("dispatcher", dispatcher);
    registration.setLoadOnStartup (1);
    registration.addMapping ("/");
  }
}
