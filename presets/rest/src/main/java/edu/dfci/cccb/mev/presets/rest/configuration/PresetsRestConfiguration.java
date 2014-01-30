package edu.dfci.cccb.mev.presets.rest.configuration;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.springframework.context.annotation.FilterType.ANNOTATION;
import static org.springframework.context.annotation.ScopedProxyMode.NO;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.Presets;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.simple.SimplePresests;
import edu.dfci.cccb.mev.presets.simple.TcgaPresetMetafile;
import edu.dfci.cccb.mev.presets.simple.TcgaPresetsBuilder;



@Log4j
@Configuration
@ComponentScan(value="edu.dfci.cccb.mev.presets", 
  includeFilters = @Filter (type = ANNOTATION, value = {Controller.class, RestController.class }))
@PropertySource ("classpath:/presets.properties")
public class PresetsRestConfiguration extends WebMvcConfigurerAdapter {

  private final static String TCGA_PROPERTY_MATA_FILENAME="mev.presets.tcga.metadata.filename";
  private final static String TCGA_PROPERTY_ROOT_FOLDER="mev.presets.tcga.metadata.root";
    
  @Inject Environment environment;
  
  @Bean  
  public Presets getTcgaPresets(@Named("tcgaPresetRoot") URL tcgaPresetRoot, TcgaPresetsBuilder builder) throws URISyntaxException, PresetException, IOException {
    String metadataFilename = environment.getProperty (TCGA_PROPERTY_MATA_FILENAME);
    log.info (TCGA_PROPERTY_ROOT_FOLDER+" URL:" + tcgaPresetRoot);
    log.info (TCGA_PROPERTY_MATA_FILENAME+":" + metadataFilename);
    
    if(metadataFilename == null)
      return new SimplePresests();
    
    
    URL metadataURL = new URL(tcgaPresetRoot, metadataFilename, null);
    log.info ("URL.getFile():" + metadataURL.getFile ());
    log.info ("URL.toString():" + metadataURL.toString ());
    log.info ("URL.getProtocol ():" + metadataURL.getProtocol ());
    
    
//    InputStream checkExists = metadataURL.openStream ();    
//    if(checkExists==null)
//      throw new PresetException ("Metadata resource not found: " + metadataURL.toString ());

    if(!checkExists(metadataURL)){
      throw new PresetException ("Metadata resource not found: " + metadataURL.toString ());
    }
    
//    File checkFile = new File(metadataURL.getFile ());
//    if( checkFile.exists ()==false)    
//      //return new SimplePresests();
//      throw new PresetException ("Metadata resource not found: " + metadataURL.toString ());

    return new SimplePresests (metadataURL, builder);
  }
  
  @Bean 
  @Scope(value=SCOPE_PROTOTYPE, proxyMode=NO)
  @Named("tcgaPreset")
  public Preset tcgaPreset(){
    return new TcgaPresetMetafile ();
  }
  @Bean
  public TcgaPresetsBuilder getTcgaPresetsBuilder(){
    return new TcgaPresetsBuilder ();
  }
  
  @Bean (name="tcgaPresetRoot")
  public URL tcgaPresetRoot() throws IOException{
    
    String pathTcgaRoot = environment.getProperty (TCGA_PROPERTY_ROOT_FOLDER);
    log.info (TCGA_PROPERTY_ROOT_FOLDER+":" + pathTcgaRoot);
    if(pathTcgaRoot == null)
      return null;
    
    if(pathTcgaRoot.endsWith ("/")==false)
      pathTcgaRoot+="/";
    
    URL tcgaPresetRootURL = new URL("file:"+pathTcgaRoot);
    
    if(!checkExists(tcgaPresetRootURL))
      return (new ClassPathResource ("tcga/")).getURL ();

    return tcgaPresetRootURL;
    
    
  }
  
  private boolean checkExists(URL url){
    try{
      url.openStream ().close ();
      return true;
    }catch(Exception e){
      return false;
    }
    
  }
}
