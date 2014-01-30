package edu.dfci.cccb.mev.presets.contract;

import java.net.URL;
import java.util.List;

import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;

public interface PresetsBuilder{

  abstract List<Preset> getAll (URL data) throws PresetException;
  Preset createPreset (Object[] values) throws PresetException;

}