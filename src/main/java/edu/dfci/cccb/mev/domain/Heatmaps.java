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
package edu.dfci.cccb.mev.domain;

import static java.lang.Runtime.getRuntime;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.dfci.cccb.mev.domain.Heatmap.Builder;

/**
 * @author levk
 * 
 */
@Component
@Scope (value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
@Log4j
public class Heatmaps implements Closeable, InitializingBean {

  private final Map<String, Heatmap> heatmaps = new HashMap<> ();

  private static Map<String, Heatmap> global = null;

  private @Autowired History history;

  public Collection<String> list () {
    return new ArrayList<String> () {
      private static final long serialVersionUID = 1L;

      {
        addAll (global.keySet ());
        addAll (heatmaps.keySet ());
      }

      /* (non-Javadoc)
       * @see java.util.ArrayList#add(java.lang.Object) */
      @Override
      public boolean add (String e) {
        return contains (e) ? false : super.add (e);
      }
    };
  }

  public Heatmap get (String id) throws HeatmapNotFoundException {
    Heatmap result = heatmaps.get (id);
    if (result == null)
      result = global.get (id);
    if (result == null)
      throw new HeatmapNotFoundException (id);
    return result;
  }

  public boolean contains (String id) {
    return heatmaps.containsKey (id) || global.containsKey (id);
  }

  public void delete (String id) throws HeatmapNotFoundException {
    if (heatmaps.remove (id) == null)
      throw new HeatmapNotFoundException (id);
  }

  public void put (String id, Heatmap heatmap) {
    history.log ("Added heatmap " + id);
    heatmaps.put (id, heatmap);
  }

  /* (non-Javadoc)
   * @see java.io.Closeable#close() */
  @Override
  public void close () throws IOException {
    for (Heatmap heatmap : heatmaps.values ())
      try {
        heatmap.close ();
      } catch (IOException | RuntimeException e) {
        log.warn ("Exception while closing " + heatmap, e);
      }
  }
  
  private @Autowired Builder builder;

  /* (non-Javadoc)
   * @see
   * org.springframework.beans.factory.InitializingBean#afterPropertiesSet() */
  @Override
  public void afterPropertiesSet () throws Exception {
    synchronized (Heatmaps.class) {
      if (global == null)
        global = new HashMap<String, Heatmap> () {
          private static final long serialVersionUID = 1L;

          {
            getRuntime ().addShutdownHook (new Thread () {
              public void run () {
                for (Heatmap heatmap : values ())
                  try {
                    heatmap.close ();
                  } catch (IOException e) {
                    log.warn ("Failed to close " + heatmap, e);
                  }
              }
            });

            new Thread () {
              public void run () {

                Properties definitions = new Properties () {
                  private static final long serialVersionUID = 1L;

                  {
                    try {
                      load (Heatmaps.class.getResourceAsStream ("/configuration/heatmap.globals.properties"));
                    } catch (IOException e) {
                      log.warn ("Unable to load global heatmaps");
                    }
                  }
                };

                for (String key : definitions.stringPropertyNames ()) {
                  File data = new File (definitions.get (key).toString ());
                  if (!data.exists ())
                    continue;
                  else
                    try {
                      put (key, builder.build (new FileInputStream (data), data.length (), key));
                    } catch (IOException e) {
                      log.warn ("Unable to load global heatmap " + key + " data at " + data, e);
                    }
                }
                log.info ("Finished loading global heatmaps");
              }
            }.start ();
          }
        };
    }
  }
}
