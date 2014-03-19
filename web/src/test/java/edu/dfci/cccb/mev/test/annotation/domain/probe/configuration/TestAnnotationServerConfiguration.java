package edu.dfci.cccb.mev.test.annotation.domain.probe.configuration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.log4j.Log4j;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.dfci.cccb.mev.annotation.domain.probe.contract.ProbeAnnotationPlatforms;
import edu.dfci.cccb.mev.annotation.domain.probe.contract.ProbeAnnotationsLoader;
import edu.dfci.cccb.mev.dataset.rest.configuration.DatasetRestConfiguration;
import edu.dfci.cccb.mev.test.annotation.server.configuration.ProbeAnnotationsPersistanceConfigTest;
import edu.dfci.cccb.mev.test.presets.rest.configuration.PresetsRestConfigurationTest;
import edu.dfci.cccb.mev.web.configuration.DispatcherConfiguration;
import edu.dfci.cccb.mev.web.configuration.PersistenceConfiguration;
import edu.dfci.cccb.mev.web.configuration.container.ContainerConfigurations;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={DispatcherConfiguration.class, 
                               PersistenceConfiguration.class, 
                               ContainerConfigurations.class, 
                               DatasetRestConfiguration.class,
                               ProbeAnnotationsPersistanceConfigTest.class,
                               PresetsRestConfigurationTest.class
                               })
public class TestAnnotationServerConfiguration {

  @Inject @Named("probe-annotations-platforms-loader") ProbeAnnotationsLoader loader;
  @Inject ProbeAnnotationPlatforms probeAnnotationPlatforms;
  
  @Test
  public void test () {
    assertNotNull (loader);
    assertNotNull (probeAnnotationPlatforms);
}


  @Autowired WebApplicationContext applicationContext;  
  private MockMvc mockMvc;
  
  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
  }
  
  @Test @Ignore
  public void testGetTcgaPresets () throws Exception {
    MvcResult result = this.mockMvc.perform(get("/presets/tcga").param ("format", "json").accept("application/json"))
            .andExpect (status ().isOk ())            
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].name").exists ())
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].disease").exists ())
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].diseaseName").exists ())
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].platform").exists ())
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].platformName").exists ())
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].dataLevel").exists ())
            .andExpect (MockMvcResultMatchers.jsonPath ("$[0].descriptor").doesNotExist ())
            .andReturn ();
    String content = result.getResponse().getContentAsString();
    log.debug ("content:"+content);
  }
  
  @Test 
  public void testPresetListView () throws Exception {
    this.mockMvc.perform(get("/container/view/elements/presets/presetList"))
    .andExpect(status().isOk())
    .andReturn ();
    
    this.mockMvc.perform(get("/container/javascript/presets/PresetManager.js"))
    .andExpect(status().isOk())
    .andReturn ();

  }
  
  
}
