/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline.impl;

import edu.kit.kastel.informalin.data.DataRepository;
import edu.kit.kastel.informalin.data.impl.TextData;
import edu.kit.kastel.informalin.pipeline.AbstractPipelineStep;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Example implementation of {@link AbstractPipelineStep}
 * 
 * @author Jan Keim
 */
public class ConcretePipelineStepOne extends AbstractPipelineStep {

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
        System.out.println("Greetings from " + this.getClass().getSimpleName() + " with id " + getId());
        var text = textData.getText();
        var tokens = Arrays.stream(text.split(" ")).toList();
        tokens = tokens.stream().filter(Predicate.not(stopwords::contains)).toList();
        textData.setTokens(tokens);
    }
}
