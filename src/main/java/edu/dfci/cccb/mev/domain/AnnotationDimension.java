package edu.dfci.cccb.mev.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors (fluent = true)
public enum AnnotationDimension {
  ROW,
  COLUMN;

  private @Getter final String text;

  private AnnotationDimension () {
    this.text = name ().toLowerCase ();
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString() */
  @Override
  public String toString () {
    return text;
  }
}
