/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline.impl;

import edu.kit.kastel.informalin.data.DataRepository;
import edu.kit.kastel.informalin.data.impl.ProcessedTextData;
import edu.kit.kastel.informalin.data.impl.TextData;
import edu.kit.kastel.informalin.pipeline.AbstractPipelineStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Example implementation of {@link AbstractPipelineStep}
 * 
 * @author Jan Keim
 */
public class ConcretePipelineStepTwoOne extends AbstractPipelineStep {

    private TextData textData;
    private ProcessedTextData processedTextData;

    public ConcretePipelineStepTwoOne(String id, DataRepository dataRepository) {
        super(id, dataRepository);
    }

    private void fetchAndInitializeData() {
        var dataRepository = getDataRepository();
        textData = dataRepository.getData("Text", TextData.class).orElseThrow();
        var processedTextDataOptional = dataRepository.getData("ProcessedTextData", ProcessedTextData.class);
        if (processedTextDataOptional.isPresent()) {
            processedTextData = processedTextDataOptional.get();
        } else {
            processedTextData = new ProcessedTextData();
            dataRepository.addData("ProcessedTextData", processedTextData);
        }
    }

    @Override
    public void run() {
        fetchAndInitializeData();
        System.out.println("Greetings from " + this.getClass().getSimpleName() + " with id " + getId());
        List<String> tokens = getTokens();
        List<String> filteredTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if ((i & 1) == 0) {
                String token = tokens.get(i);
                filteredTokens.add(token);
                System.out.print(" " + token);
            }
        }
        System.out.println("\n" + filteredTokens.size());
        processedTextData.setImportantTokens(filteredTokens);

    }

    private List<String> getTokens() {
        var tokens = processedTextData.getImportantTokens();
        if (tokens == null || tokens.isEmpty()) {
            System.out.println("No preprocessedTextData, fetching textData.");
            tokens = textData.getTokens();
        }
        return tokens;
    }

    @Override
    protected void delegateApplyConfigurationToInternalObjects(Map<String, String> additionalConfiguration) {
        // NOP
    }
}
