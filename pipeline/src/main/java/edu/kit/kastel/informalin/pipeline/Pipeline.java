/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline;

import edu.kit.kastel.informalin.data.DataRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a pipeline that can consist of multiple {@link AbstractPipelineStep AbstractPipelineSteps}.
 * Steps are executed consecutively one after another in the order they were added to the pipeline. Execution calls the
 * {@link #run()} method of the different {@link AbstractPipelineStep AbstractPipelineSteps}.
 * 
 * @author Jan Keim
 */
public class Pipeline extends AbstractPipelineStep {
    private final List<AbstractPipelineStep> pipelineSteps;

    /**
     * Constructs a Pipeline with the given id and {@link DataRepository}.
     * 
     * @param id             id for the pipeline
     * @param dataRepository {@link DataRepository} that should be used for fetching and saving data
     */
    public Pipeline(String id, DataRepository dataRepository) {
        super(id, dataRepository);
        this.pipelineSteps = new ArrayList<>();
    }

    /**
     * Constructs a Pipeline with the given id and {@link DataRepository}.
     * 
     * @param id             id for the pipeline
     * @param dataRepository {@link DataRepository} that should be used for fetching and saving data
     * @param pipelineSteps  List of {@link AbstractPipelineStep} that should be added to the constructed pipeline
     */
    public Pipeline(String id, DataRepository dataRepository, List<AbstractPipelineStep> pipelineSteps) {
        super(id, dataRepository);
        this.pipelineSteps = pipelineSteps;
    }

    /**
     * Adds a {@link AbstractPipelineStep} to the execution list of this pipeline
     * 
     * @param pipelineStep step that should be added
     * @return True, if the step was added successfully. Otherwise, returns false
     */
    public boolean addPipelineStep(AbstractPipelineStep pipelineStep) {
        return this.pipelineSteps.add(pipelineStep);
    }

    @Override
    public void run() {
        for (var pipelineStep : this.pipelineSteps) {
            logger.info("Starting {} - {}", this.getId(), pipelineStep.getId());
            var start = Instant.now();

            pipelineStep.run();

            if (logger.isInfoEnabled()) {
                var end = Instant.now();
                var duration = Duration.between(start, end);
                String durationString = String.format("%01d.%03d s", duration.toSecondsPart(), duration.toMillisPart());
                logger.info("Finished {} - {} in {}", this.getId(), pipelineStep.getId(), durationString);
            }
        }
    }

    @Override
    protected void delegateApplyConfigurationToInternalObjects(Map<String, String> additionalConfiguration) {
        this.pipelineSteps.forEach(it -> it.applyConfiguration(additionalConfiguration));
    }
}
