/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.pipeline;

import edu.kit.kastel.informalin.data.DataRepository;
import edu.kit.kastel.informalin.framework.configuration.AbstractConfigurable;

/**
 * This class represents an abstract pipeline step and defines the core functionality. Together with {@link Pipeline}
 * and concrete implementations of this class represents a composite pattern.
 */
public abstract class AbstractPipelineStep extends AbstractConfigurable {
    private final String id;
    private final DataRepository dataRepository;

    protected AbstractPipelineStep(String id, DataRepository dataRepository) {
        this.id = id;
        this.dataRepository = dataRepository;
    }

    /**
     * Run the pipeline step.
     */
    public abstract void run();

    /**
     * Returns the {@link DataRepository} that is used for saving and fetching data.
     * 
     * @return the repository for used data
     */
    protected DataRepository getDataRepository() {
        return this.dataRepository;
    }

    /**
     * Returns the id
     * 
     * @return the id
     */
    public final String getId() {
        return id;
    }
}
