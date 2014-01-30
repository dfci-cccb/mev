package edu.dfci.cccb.mev.presets.contract.exceptions;

import edu.dfci.cccb.mev.dataset.domain.contract.MevException;

public class PresetException extends MevException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public PresetException () {
  }

  public PresetException (String message) {
    super (message);
  }

  public PresetException (Throwable cause) {
    super (cause);
  }

  public PresetException (String message, Throwable cause) {
    super (message, cause);
  }

  public PresetException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super (message, cause, enableSuppression, writableStackTrace);
  }

}
