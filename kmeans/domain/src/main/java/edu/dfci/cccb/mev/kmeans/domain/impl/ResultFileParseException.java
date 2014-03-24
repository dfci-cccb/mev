package edu.dfci.cccb.mev.kmeans.domain.impl;

import lombok.Getter;

public class ResultFileParseException extends Exception {

  private static final long serialVersionUID = 1L;
  
  private @Getter String errorMsg;
  
  public ResultFileParseException(String msg){
    this.errorMsg=msg;
  }

}
