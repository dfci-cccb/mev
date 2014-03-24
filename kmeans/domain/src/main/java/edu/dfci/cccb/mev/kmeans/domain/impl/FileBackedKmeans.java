package edu.dfci.cccb.mev.kmeans.domain.impl;

import static edu.dfci.cccb.mev.kmeans.domain.prototype.AbstractKmeansBuilder.FULL_FILENAME;
import java.io.File;

import lombok.Getter;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.kmeans.domain.prototype.AbstractKmeans;

public class FileBackedKmeans extends AbstractKmeans {

  
  private @Getter final File resultFile;
  private @Getter final TemporaryFolder tempFolder;

  public FileBackedKmeans (TemporaryFolder temp) {
    this.tempFolder = temp;
    this.resultFile = new File (tempFolder, FULL_FILENAME);
  }
  
  
  @Override
  public Iterable<Node> getCentroids () {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<Node> getLeaves () {
    // TODO Auto-generated method stub
    return null;
  }

}
