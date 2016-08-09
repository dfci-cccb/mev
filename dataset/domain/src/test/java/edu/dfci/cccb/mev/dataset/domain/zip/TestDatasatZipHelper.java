package edu.dfci.cccb.mev.dataset.domain.zip;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by antony on 8/9/16.
 */
@Log4j
public class TestDatasatZipHelper {
    DatasetZipHelper helper;
    URI inputUri;
    @Before
    public void init() throws URISyntaxException {
        helper = new DatasetZipHelper();
        inputUri = TestDatasetBuilderZip.class.getResource("/edu/dfci/cccb/mev/dataset/domain/zip/mouse_test_data.tsv.zip").toURI();
    }

    @Test
    public void testExtract() throws IOException, URISyntaxException {
        File outDir = helper.extract(
                new FileInputStream(Paths.get(inputUri).toFile()),
                FilenameUtils.getBaseName(inputUri.toURL().getFile())
        );
        assertThat(outDir.listFiles().length, is(15));
        log.debug(outDir);
    }
}
