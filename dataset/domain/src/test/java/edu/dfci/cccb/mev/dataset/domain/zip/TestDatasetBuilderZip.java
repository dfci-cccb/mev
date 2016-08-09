package edu.dfci.cccb.mev.dataset.domain.zip;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dfci.cccb.mev.dataset.domain.contract.*;
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
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by antony on 8/5/16.
 */
@Log4j
public class TestDatasetBuilderZip {

    ObjectMapper mapper;
    private URI inputUri;
    private DatasetBuilderZip builder;
    @Before
    public void init() throws URISyntaxException {
        mapper = new ObjectMapper();
        builder = new DatasetBuilderZip().setMapper(new ObjectMapper());
        inputUri = TestDatasetBuilderZip.class.getResource("/edu/dfci/cccb/mev/dataset/domain/zip/mouse_test_data.tsv.zip").toURI();
    }

    @Test
    public void testBuild() throws URISyntaxException, IOException, DatasetException {
        Dataset dataset = builder.build(new ZipBinaryInput(Paths.get(inputUri).toFile()));
        log.debug(String.format("dataset: %s", dataset));

        assertThat(dataset.name(), is(FilenameUtils.getBaseName(inputUri.toURL().getFile())));
        assertThat(dataset.analyses().list().size(), is(11));

        Dimension column = dataset.dimension(Dimension.Type.COLUMN);
        assertThat(column.keys().size(), is(6));

        Dimension row = dataset.dimension(Dimension.Type.ROW);
        assertThat(row.keys().size(), is(39));

        Analysis pca = dataset.analyses().get("PCA");
        log.debug(String.format("analysis PCA analysis: %s", pca));
        String pcaJson = mapper.writeValueAsString(pca);
        log.debug(String.format("analysis PCA json: %s", pcaJson));
        assertThat(pcaJson, containsString("\"sdev\""));

        assertTrue(dataset.dimension(Dimension.Type.COLUMN).annotation() instanceof OpenRefineAnnotation );
        assertTrue(dataset.dimension(Dimension.Type.ROW).annotation() instanceof OpenRefineAnnotation );
    }
}
