package edu.dfci.cccb.mev.dataset.domain.zip;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.dfci.cccb.mev.dataset.domain.contract.*;
import edu.dfci.cccb.mev.dataset.domain.fs.DatasetBuilderFlatFile;
import edu.dfci.cccb.mev.dataset.domain.fs.FlatFileValues;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractAnnotation;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.ArrayListSelections;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDataset;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDimension;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j
@Accessors(chain = true)
public class DatasetBuilderZip extends AbstractDatasetBuilder{

  private @Inject @Setter ObjectMapper mapper;
  private List<String> getKeys(JsonNode dimension){
    ArrayNode columnKeysNode = (ArrayNode) dimension.get("keys");
    List<String> columns = new ArrayList<String>(columnKeysNode.size());
    for(JsonNode keyNode : columnKeysNode)
      columns.add(keyNode.textValue());
    return columns;
  }
  private String formatDatasetName(String name){
    return name.endsWith(".zip")
            ? FilenameUtils.getBaseName(name)
            : name;
  }

  @Override
  public Dataset build (RawInput zip) throws DatasetBuilderException {
    try{
      DatasetZipHelper dszip = new DatasetZipHelper();
      File dir = dszip.extract(zip.input(), zip.name());

      //get dataset
      File datasetFile = Paths.get(dir.getPath(), "dataset.json").toFile();
      JsonNode datasetNode = mapper.readTree(datasetFile);

      //get column keys
      JsonNode columnNode = datasetNode.get("column");
      List<String> columns = getKeys(columnNode);
      //get column selections
      Selection[] columnSelections = mapper.treeToValue(columnNode.get("selections"), Selection[].class);

      //get row keys
      JsonNode rowNode = datasetNode.get("row");
      List<String> rows = getKeys(rowNode);
      //get row selections
      Selection[] rowSelections = mapper.treeToValue(rowNode.get("selections"), Selection[].class);

      //get values
      File valuesFile = Paths.get(dir.getPath(), "values.bin").toFile();
      RawInput values = new FileBinaryInput(valuesFile);

      DatasetBuilderFlatFile builder = new DatasetBuilderFlatFile();
      Dataset dataset = builder.build(values, formatDatasetName(zip.name()), columns, rows,
              Arrays.asList(columnSelections),
              Arrays.asList(rowSelections));

      //get analyses
      for(File analysisFile : dir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith("analysis_");
        }
      })){
        JsonNode analysisNode = mapper.readTree(analysisFile);
        dataset.analyses().put(
                new AnalysisJson(analysisFile)
        );
      }
      //get annotations
      for(File annotationFile : dir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith("annotations_");
        }
      })){
        if(annotationFile.getName().endsWith("column.tar.gz"))
          dataset.dimension(Dimension.Type.COLUMN).annotation(
                  new OpenRefineAnnotation(annotationFile)
          );
        else if(annotationFile.getName().endsWith("row.tar.gz"))
          dataset.dimension(Dimension.Type.ROW).annotation(
                  new OpenRefineAnnotation(annotationFile)
          );
      }
      return dataset;

    }catch(IOException | DatasetException e){
      throw new DatasetBuilderException (String.format("Error while building dataset name from zip: %s", zip));
    }

  }

  @Override
  protected Dataset aggregate (String name, Values values, Analyses analyses, Dimension... dimensions) throws DatasetBuilderException,
                                                                                                      InvalidDatasetNameException {
    return new SimpleDataset (name, values, analyses, dimensions);
  }
}
