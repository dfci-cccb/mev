package edu.dfci.cccb.mev.presets.contract;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;

public interface PresetDatasetBuilder extends DatasetBuilder {

  Dataset build (PresetDescriptor descriptor, String datasetName, Selection columnSelection) throws PresetException;
}