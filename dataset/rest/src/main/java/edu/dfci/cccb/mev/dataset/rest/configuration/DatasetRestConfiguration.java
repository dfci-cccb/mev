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
package edu.dfci.cccb.mev.dataset.rest.configuration;

import static edu.dfci.cccb.mev.dataset.rest.assembly.tsv.prototype.AbstractTsvHttpMessageConverter.TSV_EXTENSION;
import static edu.dfci.cccb.mev.dataset.rest.assembly.tsv.prototype.AbstractTsvHttpMessageConverter.TSV_MEDIA_TYPE;
import static edu.dfci.cccb.mev.dataset.rest.resolvers.AnalysisPathVariableMethodArgumentResolver.ANALYSIS_MAPPING_NAME;
import static java.util.Arrays.asList;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;
import static org.springframework.context.annotation.ScopedProxyMode.NO;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.ToString;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

import com.fasterxml.jackson.databind.JsonSerializer;

import edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter;
import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;
import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension;
import edu.dfci.cccb.mev.dataset.domain.contract.ParserFactory;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.SelectionBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.ValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;
import edu.dfci.cccb.mev.dataset.domain.jooq.JooqBasedDatasourceValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListWorkspace;
import edu.dfci.cccb.mev.dataset.domain.simple.SharedCachedValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleSelectionBuilder;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvComposerFactory;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvParserFactory;
import edu.dfci.cccb.mev.dataset.rest.assembly.json.simple.DimensionTypeJsonSerializer;
import edu.dfci.cccb.mev.dataset.rest.assembly.json.simple.SimpleDatasetJsonSerializer;
import edu.dfci.cccb.mev.dataset.rest.assembly.json.simple.SimpleDimensionJsonSerializer;
import edu.dfci.cccb.mev.dataset.rest.assembly.json.simple.SimpleSelectionJsonSerializer;
import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.DatasetTsvMessageConverter;
import edu.dfci.cccb.mev.dataset.rest.resolvers.DatasetPathVariableMethodArgumentResolver;
import edu.dfci.cccb.mev.dataset.rest.resolvers.DimensionPathVariableMethodArgumentResolver;
import edu.dfci.cccb.mev.dataset.rest.resolvers.SelectionPathVariableMethodArgumentResolver;

/**
 * @author levk
 * 
 */
@Configuration
@ComponentScan (basePackages = "edu.dfci.cccb.mev.dataset.rest.controllers")
@ToString
public class DatasetRestConfiguration extends MevRestConfigurerAdapter {

  // Domain conversational objects

  @Bean
  @Scope (value = SCOPE_SESSION, proxyMode = INTERFACES)
  public Workspace workspace () {
    return new ArrayListWorkspace ();
  }

  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = NO)
  public Dataset dataset (NativeWebRequest request, DatasetPathVariableMethodArgumentResolver resolver) throws Exception {
    return resolver.resolveObject (request);
  }

  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = NO)
  public Dimension dimension (NativeWebRequest request, DimensionPathVariableMethodArgumentResolver resolver) throws Exception {
    return resolver.resolveObject (request);
  }

  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = NO)
  public Selection selection (NativeWebRequest request, SelectionPathVariableMethodArgumentResolver resolver) throws Exception {
    return resolver.resolveObject (request);
  }

  // FIXME: this is a hack until type of analysis goes into the request mapping,
  // regarding issue #442
  @SuppressWarnings ("unchecked")
  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = NO)
  public Analysis analysis (NativeWebRequest request, Dataset dataset) throws Exception {
    return dataset.analyses ()
                  .get (((Map<String, String>) request.getAttribute (URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                                                                     RequestAttributes.SCOPE_REQUEST)).get (ANALYSIS_MAPPING_NAME));
  }

  // Domain builders

  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = INTERFACES)
  public ValueStoreBuilder valueFactory (DataSource dataSource) throws Exception {
    return new SharedCachedValueStoreBuilder (new JooqBasedDatasourceValueStoreBuilder (dataSource));
  }

  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = INTERFACES)
  public DatasetBuilder datasetBuilder () {
    return new SimpleDatasetBuilder ();
  }

  @Bean
  @Scope (value = SCOPE_REQUEST, proxyMode = INTERFACES)
  public SelectionBuilder selectionBuilder () {
    return new SimpleSelectionBuilder ();
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.prototype.MevRestConfigurerAdapter#
   * addJsonSerializers(java.util.List) */
  @Override
  public void addJsonSerializers (List<JsonSerializer<?>> serializers) {
    serializers.addAll (asList (new DimensionTypeJsonSerializer (),
                                new SimpleDatasetJsonSerializer (),
                                new SimpleDimensionJsonSerializer (),
                                new SimpleSelectionJsonSerializer ()));
  }

  /* (non-Javadoc)
   * @see edu.dfci.cccb.mev.dataset.rest.prototype.MevRestConfigurerAdapter#
   * addHttpMessageConverters(java.util.List) */
  @Override
  public void addHttpMessageConverters (List<HttpMessageConverter<?>> converters) {
    converters.add (new DatasetTsvMessageConverter ());
  }

  @Bean
  public SuperCsvComposerFactory superCsvComposerFactory () {
    return new SuperCsvComposerFactory ();
  }

  @Bean
  public ParserFactory tsvParserFactory () {
    return new SuperCsvParserFactory ();
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addPreferredArgumentResolvers(java.util.List) */
  @Override
  public void addPreferredArgumentResolvers (List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.addAll (asList (new SelectionPathVariableMethodArgumentResolver (),
                              datasetPathVariableMethodArgumentResolver (),
                              dimensionPathVariableMethodArgumentResolver ()));
  }

  @Bean
  public DatasetPathVariableMethodArgumentResolver datasetPathVariableMethodArgumentResolver () {
    return new DatasetPathVariableMethodArgumentResolver ();
  }

  @Bean
  public DimensionPathVariableMethodArgumentResolver dimensionPathVariableMethodArgumentResolver () {
    return new DimensionPathVariableMethodArgumentResolver ();
  }

  /* (non-Javadoc)
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
   * #configureContentNegotiation
   * (org.springframework.web.servlet.config.annotation
   * .ContentNegotiationConfigurer) */
  @Override
  public void configureContentNegotiation (ContentNegotiationConfigurer configurer) {
    configurer.mediaType (TSV_EXTENSION, TSV_MEDIA_TYPE);
  }
}
