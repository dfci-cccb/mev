package edu.dfci.cccb.mev.presets.contract.exceptions;

import edu.dfci.cccb.mev.dataset.domain.contract.MevException;

public class PresetNotFoundException extends MevException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public PresetNotFoundException () {
  }

  public PresetNotFoundException (String message) {
    super (message);
  }

  public PresetNotFoundException (Throwable cause) {
    super (cause);
  }

  public PresetNotFoundException (String message, Throwable cause) {
    super (message, cause);
  }

  public PresetNotFoundException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super (message, cause, enableSuppression, writableStackTrace);
  }

}
