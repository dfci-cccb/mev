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
package edu.dfci.cccb.mev;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.jetty.annotations.AbstractDiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationDecorator;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.annotations.ClassNameResolver;
import org.eclipse.jetty.annotations.AnnotationParser.DiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.WebFilterAnnotationHandler;
import org.eclipse.jetty.annotations.WebListenerAnnotationHandler;
import org.eclipse.jetty.annotations.WebServletAnnotationHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author levk
 * 
 */
public class _main {

  public static void main (String[] args) throws Exception {

    Server server = new Server (0) {
      {
        setHandler (new WebAppContext () {
          {
            setWar (new Properties () {
              private static final long serialVersionUID = 1L;

              {
                load (ClassLoader.getSystemClassLoader ().getResourceAsStream ("test.properties"));
              }
            }.getProperty ("war.path"));
            setParentLoaderPriority (true);
            setContextPath ("/");
            setConfigurations (new Configuration[] {
                                                    new AnnotationConfiguration () {
                                                      public void configure (WebAppContext context) throws Exception {
                                                        boolean metadataComplete = context.getMetaData ()
                                                                                          .isMetaDataComplete ();
                                                        context.addDecorator (new AnnotationDecorator (context));

                                                        AnnotationParser parser = null;
                                                        if (!metadataComplete
                                                            && (context.getServletContext ()
                                                                       .getEffectiveMajorVersion () >= 3
                                                            || context.isConfigurationDiscovered ())) {
                                                          _discoverableAnnotationHandlers.add (new WebServletAnnotationHandler (context));
                                                          _discoverableAnnotationHandlers.add (new WebFilterAnnotationHandler (context));
                                                          _discoverableAnnotationHandlers.add (new WebListenerAnnotationHandler (context));
                                                        }

                                                        createServletContainerInitializerAnnotationHandlers (context,
                                                                                                             getNonExcludedInitializers (context));

                                                        if (!_discoverableAnnotationHandlers.isEmpty ()
                                                            || _classInheritanceHandler != null
                                                            || !_containerInitializerAnnotationHandlers.isEmpty ()) {
                                                          parser = createAnnotationParser ();

                                                          parse (context, parser);

                                                          for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
                                                            context.getMetaData ()
                                                                   .addDiscoveredAnnotations (((AbstractDiscoverableAnnotationHandler) h).getAnnotationList ());
                                                        }

                                                      }

                                                      private void parse (final WebAppContext context,
                                                                          AnnotationParser parser) throws Exception {
                                                        List<Resource> _resources =
                                                                                    getResources (getClass ().getClassLoader ());

                                                        for (Resource _resource : _resources) {
                                                          if (_resource == null)
                                                            return;

                                                          parser.clearHandlers ();
                                                          for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
                                                            if (h instanceof AbstractDiscoverableAnnotationHandler)
                                                              ((AbstractDiscoverableAnnotationHandler) h).setResource (null);
                                                          parser.registerHandlers (_discoverableAnnotationHandlers);
                                                          parser.registerHandler (_classInheritanceHandler);
                                                          parser.registerHandlers (_containerInitializerAnnotationHandlers);

                                                          parser.parse (_resource,
                                                                        new ClassNameResolver () {
                                                                          public boolean isExcluded (String name) {
                                                                            if (context.isSystemClass (name))
                                                                              return true;
                                                                            if (context.isServerClass (name))
                                                                              return false;
                                                                            return false;
                                                                          }

                                                                          public boolean shouldOverride (String name) {
                                                                            if (context.isParentLoaderPriority ())
                                                                              return false;
                                                                            return true;
                                                                          }
                                                                        });
                                                        }
                                                      }

                                                      private List<Resource> getResources (ClassLoader aLoader) throws IOException {
                                                        if (aLoader instanceof URLClassLoader) {
                                                          List<Resource> _result = new ArrayList<Resource> ();
                                                          URL[] _urls = ((URLClassLoader) aLoader).getURLs ();
                                                          for (URL _url : _urls)
                                                            _result.add (Resource.newResource (_url));

                                                          return _result;
                                                        }
                                                        return Collections.emptyList ();
                                                      }
                                                    }
            });
          }
        });
        setThreadPool (new QueuedThreadPool () {
          {
            setMinThreads (5);
            setMaxThreads (10);
          }
        });
      }
    };

    server.start ();
    int port = server.getConnectors ()[0].getLocalPort ();

    System.out.println (port);

    Thread.sleep (100000);

    server.stop ();
  }
}
