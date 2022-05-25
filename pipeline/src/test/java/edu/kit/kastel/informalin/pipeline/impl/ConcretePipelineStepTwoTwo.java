/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline.impl;

import edu.kit.kastel.informalin.data.DataRepository;
import edu.kit.kastel.informalin.data.impl.ProcessedTextData;
import edu.kit.kastel.informalin.data.impl.ResultData;
import edu.kit.kastel.informalin.pipeline.AbstractPipelineStep;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Example implementation of {@link AbstractPipelineStep}
 * 
 * @author Jan Keim
 */
public class ConcretePipelineStepTwoTwo extends AbstractPipelineStep {

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
        System.out.println("Greetings from " + this.getClass().getSimpleName() + " with id " + getId());
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
