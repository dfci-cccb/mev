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
package edu.dfci.cccb.mev.dataset.rest.assembly.tsv;

import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractRawInput;

/**
 * @author levk
 * 
 */
public class UrlTsvInput extends AbstractRawInput {

  private URL url;

  public UrlTsvInput (URL url) {
    if (url == null)
      throw new NullPointerException ("URL cannot be null");
    this.url = url;
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.domain.contract.RawInput#contentType() */
  @Override
  public String contentType () {
    return TAB_SEPARATED_VALUES;
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.domain.contract.RawInput#name() */
  @Override
  public String name () {    
    return super.name ()!=null ? super.name() : getBaseName (url.toString ());
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.domain.contract.RawInput#input() */
  @Override
  public InputStream input () throws IOException {
    return url.openStream ();
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.domain.contract.RawInput#size() */
  @Override
  public long size () {
    return -1;
  }
}
