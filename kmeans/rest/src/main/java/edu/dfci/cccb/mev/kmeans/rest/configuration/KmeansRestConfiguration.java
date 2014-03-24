package edu.dfci.cccb.mev.kmeans.rest.configuration;

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
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans;
import edu.dfci.cccb.mev.kmeans.domain.contract.KmeansBuilder;
import edu.dfci.cccb.mev.kmeans.domain.impl.FileBackedKmeansBuilder;
import edu.dfci.cccb.mev.kmeans.rest.assembly.json.KmeansJsonSerializer;
import edu.dfci.cccb.mev.kmeans.rest.assembly.json.NodeJsonSerializer;
import edu.dfci.cccb.mev.kmeans.rest.assembly.tsv.KmeansTsvMessageConverter;

@ToString
@Configuration
@ComponentScan ("edu.dfci.cccb.mev.limma.rest.controllers")
public class KmeansRestConfiguration extends MevRestConfigurerAdapter{
  @Bean
  @Scope (SCOPE_REQUEST)
  public KmeansBuilder kmeansBuilder () {
    return new  FileBackedKmeansBuilder();
  }

  @Bean (name = "R")
  public ScriptEngine r () {
    return new ScriptEngineManager ().getEngineByName ("CliR");
  }

  @Bean
  public AnalysisPathVariableMethodArgumentResolver<Kmeans> limmaPathVariableMethodArgumentResolver () {
    return new AnalysisPathVariableMethodArgumentResolver<> (Kmeans.class);
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addJsonSerializers(java.util.List) */
  @Override
  public void addJsonSerializers (List<JsonSerializer<?>> serializers) {
    serializers.addAll (asList (new NodeJsonSerializer (), new KmeansJsonSerializer ()));
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addHttpMessageConverters(java.util.List) */
  @Override
  public void addHttpMessageConverters (List<HttpMessageConverter<?>> converters) {
    converters.add (new KmeansTsvMessageConverter ());
  }
}
