package edu.dfci.cccb.mev.presets.contract;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreType;


@JsonIgnoreType(value=true)
public interface PresetDescriptor {
  URL dataUrl();
  URL columnUrl();
}
