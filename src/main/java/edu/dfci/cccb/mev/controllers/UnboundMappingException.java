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
package edu.dfci.cccb.mev.controllers;

import java.util.ResourceBundle;

import lombok.Getter;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author levk
 *
 */
public class UnboundMappingException extends Exception {
  private static final long serialVersionUID = 1L;

  private final @Getter String url;
  
  public UnboundMappingException (String url) {
    super ("Unbound URL" + url);
    this.url = url;
  }
  
  public UnboundMappingException () {
    super ("Unbound URL");
    url = null;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Throwable#getLocalizedMessage()
   */
  @Override
  public String getLocalizedMessage () {
    return ResourceBundle.getBundle ("i18n.home", LocaleContextHolder.getLocale ()).getString ("global.unbound");
  }
}
