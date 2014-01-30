package edu.dfci.cccb.mev.presets.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.Presets;

@RestController
@RequestMapping(value="/presets")
public class PresetsRestController {

  @Inject Presets presets;
  
  @RequestMapping(value="/tcga")
  public List<Preset> getTcgaPresets(){
    return presets.getAll ();
  }
  
}
