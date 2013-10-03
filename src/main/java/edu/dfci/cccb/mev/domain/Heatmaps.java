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

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author levk
 * 
 */
@Component
@Scope (value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
@Log4j
public class Heatmaps implements Closeable {

  private final Map<String, Heatmap> heatmaps = new HashMap<> ();
  
  private @Autowired History history;

  public Collection<String> list () {
    return heatmaps.keySet ();
  }

  public Heatmap get (String id) throws HeatmapNotFoundException {
    Heatmap result = heatmaps.get (id);
    if (result == null)
      throw new HeatmapNotFoundException (id);
    return result;
  }
  
  public boolean contains (String id) {
    return heatmaps.containsKey (id);
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
   * @see java.io.Closeable#close()
   */
  @Override
  public void close () throws IOException {
    for (Heatmap heatmap : heatmaps.values ())
      try {
        heatmap.close ();
      } catch (IOException | RuntimeException e) {
        log.warn ("Exception while closing " + heatmap, e);
      }
  }
}
