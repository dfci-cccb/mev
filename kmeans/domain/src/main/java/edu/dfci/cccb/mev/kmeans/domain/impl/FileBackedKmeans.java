package edu.dfci.cccb.mev.kmeans.domain.impl;

import static edu.dfci.cccb.mev.kmeans.domain.prototype.AbstractKmeansBuilder.FULL_FILENAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.SneakyThrows;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.kmeans.domain.prototype.AbstractKmeans;

public class FileBackedKmeans extends AbstractKmeans implements AutoCloseable{

  
  private @Getter final File resultFile;
  private @Getter final TemporaryFolder tempFolder;

  public FileBackedKmeans (TemporaryFolder temp) {
    this.tempFolder = temp;
    this.resultFile = new File (tempFolder, FULL_FILENAME);
  }
  
  
  @Override
  public Iterable<Node> getAllNodes () {
    return extractData ();
  }

  /*
   * Parses the result file to extract the node data. 
   */
  @SneakyThrows (IOException.class)
  private Iterable<Node> extractData(){
    
    BufferedReader reader = new BufferedReader (new FileReader (resultFile));
    try{
      int centroidCount=Integer.parseInt (reader.readLine ());  
      Collection<Node> nodes=new ArrayList<Node>();
      for(int i=0; i<centroidCount; i++){ 
        nodes.add (parseLine(reader.readLine (), GraphNode.CENTROID));
      }
      int leafCount=Integer.parseInt (reader.readLine ());  
      for(int i=0; i<leafCount; i++){ 
        nodes.add (parseLine(reader.readLine (), GraphNode.LEAF));
      }
      return nodes;
      
    }
    catch(ResultFileParseException | IllegalArgumentException e2){
      //do something here!!!
    }
    finally{
      reader.close();
    }
    return null;
  }

  @Override
  public void close () throws Exception {
    tempFolder.close ();
  }
  
  /*
   * Reads a line in the result file and parses it for the appropriate Node object.
   */
  private Node parseLine(String line, String nodeType) throws ResultFileParseException{
    if (line!=null){
      String[] contents=line.split ("\t");
      if(contents.length==4){
        try{
          return new GraphNode (contents[0],nodeType, Integer.parseInt (contents[1]), Double.parseDouble (contents[2]),Double.parseDouble (contents[3]));
          }
        catch(NumberFormatException ex){
          throw new ResultFileParseException("The fields in the line could not be coerced to the proper types");
        }      
      }
      else{
        throw new ResultFileParseException("The line does not have the proper number of fields (4).");
      }
    }
    else{
      throw new ResultFileParseException("The line in the result file is given as 'null'");
    }
  }

}
