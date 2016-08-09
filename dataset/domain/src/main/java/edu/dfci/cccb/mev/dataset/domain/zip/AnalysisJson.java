package edu.dfci.cccb.mev.dataset.domain.zip;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;
import edu.dfci.cccb.mev.dataset.domain.prototype.AbstractAnalysis;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by antony on 8/8/16.
 */
@JsonSerialize(using = AnalysisJsonSerializer.class)
public class AnalysisJson extends AbstractAnalysis<AnalysisJson> implements Analysis{

    private File file;
    private String sAnalysis = null;
    private void parse(JsonNode node){
        name(node.get("name").textValue());
        type(node.get("type").textValue());

        JsonNode statusNode = node.get("status");
        if(statusNode!=null)
            status(statusNode.textValue());

        JsonNode errorNode = node.get("status");
        if(errorNode!=null)
            error(errorNode.textValue());
    }

    public AnalysisJson(File file) throws IOException {
        parse((new ObjectMapper()).readTree(file));
        sAnalysis = IOUtils.toString(new FileInputStream(file));
    }

    public String toJson(){
        return this.sAnalysis;
    }
}
