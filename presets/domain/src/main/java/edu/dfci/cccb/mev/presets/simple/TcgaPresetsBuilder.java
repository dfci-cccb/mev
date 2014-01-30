package edu.dfci.cccb.mev.presets.simple;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.prototype.AbstractPresetsBuilder;

public class TcgaPresetsBuilder extends AbstractPresetsBuilder  {
    
  @Inject @Named("tcgaPreset") Provider<Preset> tcgaPresetProvider;
  
  @Override
  public Preset createPreset (Object[] values) throws PresetException{
    Preset newPreset = tcgaPresetProvider.get ();
    return newPreset.init(values);
  }
  
}
