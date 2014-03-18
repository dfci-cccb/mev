package edu.dfci.cccb.mev.t_test.domain.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest.Entry;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Accessors (fluent = true)
public class SimpleEntry implements Entry{
  
  private @Getter final double pValue;
  private @Getter final String geneId;
  
}
