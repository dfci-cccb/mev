package edu.dfci.cccb.mev.dataset.domain.zip;

import edu.dfci.cccb.mev.dataset.domain.contract.Annotation;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * Created by antony on 8/8/16.
 */
@Accessors(fluent = true)
public class OpenRefineAnnotation implements Annotation {
    @Getter private final File file;
    public OpenRefineAnnotation(File file){
        this.file = file;
    }
}
