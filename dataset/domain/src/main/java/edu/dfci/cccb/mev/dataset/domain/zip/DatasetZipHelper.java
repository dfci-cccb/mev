package edu.dfci.cccb.mev.dataset.domain.zip;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.dfci.cccb.mev.dataset.domain.contract.*;
import edu.dfci.cccb.mev.dataset.domain.fs.DatasetBuilderFlatFile;
import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by antony on 8/5/16.
 */
@Log4j
@NoArgsConstructor
@Accessors(chain = true)
public class DatasetZipHelper {

    public File extract(InputStream in, String name) throws IOException {

        TemporaryFolder tmpFolder = new TemporaryFolder(
                String.format("unzip_%s",name));
        try(ZipInputStream zin = new ZipInputStream(new BufferedInputStream(in))){
            final int BUFFER = 2048;
            ZipEntry entry;

            while((entry = zin.getNextEntry()) != null) {
                log.info("Extracting: " +entry);
                try(FileOutputStream fos = new FileOutputStream(
                    Paths.get(
                            tmpFolder.getPath(),
                            entry.getName()
                    ).toFile())
                ){
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                    IOUtils.copy(zin, dest);
                    dest.flush();
                }
            }
        }
        return tmpFolder;
    }
}
