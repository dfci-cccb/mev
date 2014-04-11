package edu.dfci.cccb.mev.presets.tcga;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetDescriptor;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.prototype.AbstractTcgaPreset;
import edu.dfci.cccb.mev.presets.simple.SimplePresetDescriptor;

@Accessors(fluent=true)
@EqualsAndHashCode
@ToString
public class TcgaPresetMetafile extends AbstractTcgaPreset {

  @JsonIgnore(value=true) private @Setter  @Inject @Named ("tcgaPresetRoot") URL tcgaPresetRoot;
  @JsonIgnore(value=true) private @Setter  @Inject @Named ("probe-annotations-root") URL rowAnnotationsRoot;  
  @JsonIgnore(value=true) private String filename;
  @JsonIgnore(value=true) private String path;
  @JsonProperty(value="name") private @Getter String name;
  @JsonProperty(value="disease") private @Getter String disease;
  @JsonProperty(value="diseaseName") private @Getter String diseaseName;
  @JsonProperty(value="platform") private @Getter String platform;
  @JsonProperty(value="platformName") private @Getter String platformName;
  @JsonProperty("dataLevel") private @Getter String dataLevel;
  private @Getter String scale;
  //@Getter(onMethod = @_ (@JsonProperty (value="platformName")) 
  @JsonIgnore private @Getter PresetDescriptor descriptor;
  
  public TcgaPresetMetafile(){}
  
  @Override
  public Preset init(Object[] values) throws PresetException{
      return init(
          (String)values[0],
          (String)values[1],    
          (String)values[2],
          (String)values[3],
          (String)values[4],
          (String)values[5],
          (String)values[6],
          (String)values[7]
        );
  }
  
  public Preset init(String filename, String path, String disease, String diseaseName, String platform, String platformName, String dataLevel, String scale) throws PresetException{
    this.filename=filename;
    this.path=path;
    this.name=filename;
    this.disease=disease;
    this.diseaseName=diseaseName;
    this.platform=platform;
    this.platformName=platformName;
    this.dataLevel=dataLevel;
    this.scale=scale;
    this.descriptor = new SimplePresetDescriptor ("PRESET-"+name,tcgaPresetRoot, getDataUrlSpec (), getColumnUrlSpec (), rowAnnotationsRoot, getRowUrlSpec ()); 
    return this;
  }
   
  protected String getDataUrlSpec() {
    return "tcga_data/"+path+"/"+filename;
  }
  protected String getColumnUrlSpec() {
    //ACC-clinical_annotations-tsv.openrefine.tar.gz
    return "openrefine/clinical/"+disease+"-clinical_annotations-tsv.openrefine.tar.gz";
  }
  protected String getRowUrlSpec() {
    //ACC-clinical_annotations-tsv.openrefine.tar.gz
    if(this.dataLevel.trim().equalsIgnoreCase ("level_3"))
      //geneSymbol_goAnnotations-tsv.google-refine.tar.gz
      return "openrefine/geneSymbol_goAnnotations-tsv.google-refine.tar.gz";
    else
      //HG-U133_Plus_2-na33-annot-out-tsv.google-refine.tar.gz
      return "openrefine/"+this.platform+"-na33-annot-out-tsv.google-refine.tar.gz";              
  }

  
}
  
