/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline.impl;

import edu.kit.kastel.informalin.data.DataRepository;
import edu.kit.kastel.informalin.data.impl.TextData;
import edu.kit.kastel.informalin.pipeline.AbstractPipelineStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Example implementation of {@link AbstractPipelineStep}
 */
public class ConcretePipelineStepOne extends AbstractPipelineStep {
    private static final Logger logger = LoggerFactory.getLogger(ConcretePipelineStepOne.class);

    private TextData textData;
    private final List<String> stopwords = List.of("is", "an", ".", "This");

    public ConcretePipelineStepOne(String id, DataRepository dataRepository) {
        super(id, dataRepository);
    }

    private void fetchData() {
        this.textData = getDataRepository().getData("Text", TextData.class).orElseThrow();
    }

    @Override
    public void run() {
        fetchData();
        logger.info("Greetings from {} with id {}", this.getClass().getSimpleName(), getId());
        var text = textData.getText();
        var tokens = Arrays.stream(text.split(" ")).toList();
        tokens = tokens.stream().filter(Predicate.not(stopwords::contains)).toList();
        textData.setTokens(tokens);
    }

    @Override
    protected void delegateApplyConfigurationToInternalObjects(Map<String, String> additionalConfiguration) {
        // NOP
    }
}
