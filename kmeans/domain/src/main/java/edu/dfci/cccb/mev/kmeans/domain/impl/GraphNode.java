package edu.dfci.cccb.mev.kmeans.domain.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.kmeans.domain.contract.Kmeans.Node;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor 
@Accessors (fluent = true)
public class GraphNode implements Node{
  
  public static final String CENTROID="CENTROID";
  public static final String LEAF="LEAF";

  private @Getter final String id;
  private @Getter final String type;
  private @Getter final int cluster;
  private @Getter final double x;
  private @Getter final double y;

}
