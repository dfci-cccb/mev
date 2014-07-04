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
package edu.dfci.cccb.mev.limma.rest.assembly.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.AbstractAnalysisJsonSerializer;
import edu.dfci.cccb.mev.limma.domain.contract.Limma;
import edu.dfci.cccb.mev.limma.domain.contract.Limma.Entry;

/**
 * @author levk
 * 
 */
public class LimmaJsonSerializer extends AbstractAnalysisJsonSerializer<Limma> {

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer#handledType() */
  @Override
  public Class<Limma> handledType () {
    return Limma.class;
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.assembly.json.prototype.
   * AbstractAnalysisJsonSerializer
   * #serializeAnalysisContent(edu.dfci.cccb.mev.dataset
   * .domain.contract.Analysis, com.fasterxml.jackson.core.JsonGenerator,
   * com.fasterxml.jackson.databind.SerializerProvider) */
  @Override
  protected void serializeAnalysisContent (Limma value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                                                                                                        JsonProcessingException {
    super.serializeAnalysisContent (value, jgen, provider);
    jgen.writeArrayFieldStart ("results");
    for (Entry e : value.full ())
      provider.defaultSerializeValue (e, jgen);
    jgen.writeEndArray ();
    provider.defaultSerializeField ("topGo", value.topGo (), jgen);
    provider.defaultSerializeField ("control", value.control (), jgen);
    provider.defaultSerializeField ("experiment", value.experiment (), jgen);
  }
}
