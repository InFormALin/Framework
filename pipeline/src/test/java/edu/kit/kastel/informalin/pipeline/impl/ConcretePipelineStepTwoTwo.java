/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline.impl;

import edu.kit.kastel.informalin.data.DataRepository;
import edu.kit.kastel.informalin.data.impl.ProcessedTextData;
import edu.kit.kastel.informalin.data.impl.ResultData;
import edu.kit.kastel.informalin.pipeline.AbstractPipelineStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Example implementation of {@link AbstractPipelineStep}
 */
public class ConcretePipelineStepTwoTwo extends AbstractPipelineStep {
    private static final Logger logger = LoggerFactory.getLogger(ConcretePipelineStepTwoTwo.class);

    private ProcessedTextData processedTextData;
    private ResultData resultData;

    public ConcretePipelineStepTwoTwo(String id, DataRepository dataRepository) {
        super(id, dataRepository);
    }

    private void fetchAndInitializeData() {
        var dataRepository = getDataRepository();
        processedTextData = dataRepository.getData("ProcessedTextData", ProcessedTextData.class).orElseThrow();
        resultData = new ResultData();
        dataRepository.addData("ResultData", resultData);
    }

    @Override
    public void run() {
        fetchAndInitializeData();
        logger.info("Greetings from {} with id {}", this.getClass().getSimpleName(), getId());
        var tokens = processedTextData.getImportantTokens();
        var tokenWithLength = tokens.stream().collect(Collectors.toMap(e -> e, String::length, (o1, o2) -> o1, TreeMap::new));
        var firstEntry = tokenWithLength.firstKey();
        resultData.setResult(firstEntry);
    }

    @Override
    protected void delegateApplyConfigurationToInternalObjects(Map<String, String> additionalConfiguration) {
        // NOP
    }
}
