/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.data;

import java.util.Optional;

/**
 * This abstract class defines data that is used for the pipeline steps.
 * 
 * @author Jan Keim
 */
public abstract class PipelineStepData {

    /**
     * Checks if this data can be cast into the given {@link PipelineStepData} class.
     * 
     * @param clazz class the data should be cast into
     * @return true, if casting is possible. Else, false
     * @param <T> class type of the preferred data type
     */
    public <T extends PipelineStepData> boolean isClass(Class<T> clazz) {
        return (this.getClass().equals(clazz));
    }

    /**
     * Converts this data into a given {@link PipelineStepData} class and returns the converted class, packed in an
     * {@link Optional}. If conversion is impossible, returns an empty {@link Optional}.
     * 
     * @param clazz class this should be converted to
     * @return Optional containing the converted class or that is empty, if conversion failed.
     * @param <T> {@link PipelineStepData} type
     */
    public <T extends PipelineStepData> Optional<T> convertToClass(Class<T> clazz) {
        if (!this.isClass(clazz)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(clazz.cast(this));
        }
    }
}
