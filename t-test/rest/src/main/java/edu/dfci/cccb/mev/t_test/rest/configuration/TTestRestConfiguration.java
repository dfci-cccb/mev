package edu.dfci.cccb.mev.t_test.rest.configuration;

import static java.util.Arrays.asList;

import java.util.List;

import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.databind.JsonSerializer;

import edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter;
import edu.dfci.cccb.mev.dataset.rest.resolvers.AnalysisPathVariableMethodArgumentResolver;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;
import edu.dfci.cccb.mev.t_test.domain.contract.TTestBuilder;
import edu.dfci.cccb.mev.t_test.domain.impl.OneSampleTTestBuilder;
import edu.dfci.cccb.mev.t_test.domain.impl.PairedTTestBuilder;
import edu.dfci.cccb.mev.t_test.domain.impl.TwoSampleTTestBuilder;
import edu.dfci.cccb.mev.t_test.rest.assembly.json.EntryJsonSerializer;
import edu.dfci.cccb.mev.t_test.rest.assembly.json.TTestJsonSerializer;
import edu.dfci.cccb.mev.t_test.rest.assembly.tsv.TTestTsvMessageConverter;

@Configuration
@ComponentScan ("edu.dfci.cccb.mev.t_test.rest.controllers")

public class TTestRestConfiguration extends MevRestConfigurerAdapter{

  @Bean
  @Named ("one.sample.t-test.builder")
  public TTestBuilder oneSampleTTestBuilder () {
    return new OneSampleTTestBuilder ();
  }
  
  @Bean
  @Named ("two.sample.t-test.builder")
  public TTestBuilder twoSampleTTestBuilder () {
    return new TwoSampleTTestBuilder ();
  }
  
  @Bean
  @Named ("paired.t-test.builder")
  public TTestBuilder pairedSampleTTestBuilder () {
    return new PairedTTestBuilder ();
  }
  
  @Bean (name = "R")
  public ScriptEngine r () {
    return new ScriptEngineManager ().getEngineByName ("CliR");
  }

  @Bean
  public AnalysisPathVariableMethodArgumentResolver<TTest> tTestPathVariableMethodArgumentResolver () {
    return new AnalysisPathVariableMethodArgumentResolver<> (TTest.class);
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addJsonSerializers(java.util.List) */
  @Override
  public void addJsonSerializers (List<JsonSerializer<?>> serializers) {
    serializers.addAll (asList (new EntryJsonSerializer (), new TTestJsonSerializer ()));
  }

  /* (non-Javadoc)
   * @see
   * edu.dfci.cccb.mev.configuration.rest.prototype.MevRestConfigurerAdapter
   * #addHttpMessageConverters(java.util.List) */
  @Override
  public void addHttpMessageConverters (List<HttpMessageConverter<?>> converters) {
    converters.add (new TTestTsvMessageConverter ());
  }
}
