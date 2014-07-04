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
package edu.dfci.cccb.mev.limma.rest.configuration;

import static java.util.Arrays.asList;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lombok.ToString;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.databind.JsonSerializer;

import edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter;
import edu.dfci.cccb.mev.dataset.rest.resolvers.AnalysisPathVariableMethodArgumentResolver;
import edu.dfci.cccb.mev.limma.domain.contract.Limma;
import edu.dfci.cccb.mev.limma.domain.contract.LimmaBuilder;
import edu.dfci.cccb.mev.limma.domain.simple.StatelessScriptEngineFileBackedLimmaBuilder;
import edu.dfci.cccb.mev.limma.rest.assembly.json.EntryJsonSerializer;
import edu.dfci.cccb.mev.limma.rest.assembly.json.GoEntryJsonSerializer;
import edu.dfci.cccb.mev.limma.rest.assembly.json.LimmaJsonSerializer;
import edu.dfci.cccb.mev.limma.rest.assembly.tsv.LimmaTsvMessageConverter;

/**
 * @author levk
 * 
 */
@ToString
@Configuration
@ComponentScan ("edu.dfci.cccb.mev.limma.rest.controllers")
public class LimmaRestConfiguration extends MevRestConfigurerAdapter {

  @Bean
  @Scope (SCOPE_REQUEST)
  public LimmaBuilder limmaBuilder () {
    return new StatelessScriptEngineFileBackedLimmaBuilder ();
  }

  @Bean (name = "R")
  public ScriptEngine r () {
    return new ScriptEngineManager ().getEngineByName ("CliR");
  }

  @Bean
  public AnalysisPathVariableMethodArgumentResolver<Limma> limmaPathVariableMethodArgumentResolver () {
    return new AnalysisPathVariableMethodArgumentResolver<> (Limma.class);
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addJsonSerializers(java.util.List) */
  @Override
  public void addJsonSerializers (List<JsonSerializer<?>> serializers) {
    serializers.addAll (asList (new EntryJsonSerializer (), new LimmaJsonSerializer (), new GoEntryJsonSerializer ()));
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addHttpMessageConverters(java.util.List) */
  @Override
  public void addHttpMessageConverters (List<HttpMessageConverter<?>> converters) {
    converters.add (new LimmaTsvMessageConverter ());
  }
}
