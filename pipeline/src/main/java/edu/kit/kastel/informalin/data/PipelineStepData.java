/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Optional;

/**
 * This abstract class defines data that is used for the pipeline steps.
 *
 * @author Jan Keim
 */
public interface PipelineStepData extends Serializable {

    Logger logger = LoggerFactory.getLogger(PipelineStepData.class);

    /**
     * Checks if this data can be cast into the given {@link PipelineStepData} class.
     *
     * @param clazz class the data should be cast into
     * @param <T>   class type of the preferred data type
     * @return true, if casting is possible. Else, false
     */
    default <T extends PipelineStepData> boolean isClass(Class<T> clazz) {
        return clazz.isAssignableFrom(this.getClass());
    }

    /**
     * Converts this data into a given {@link PipelineStepData} class and returns the converted class, packed in an
     * {@link Optional}. If conversion is impossible, returns an empty {@link Optional}.
     *
     * @param clazz class this should be converted to
     * @param <T>   {@link PipelineStepData} type
     * @return Optional containing the converted class or that is empty, if conversion failed.
     */
    default <T extends PipelineStepData> Optional<T> convertToClass(Class<T> clazz) {
        if (!this.isClass(clazz)) {
            return Optional.empty();
        } else {
            return Optional.of(clazz.cast(this));
        }
    }

    default String serialize() {
        var oom = createObjectMapper();
        try {
            return oom.writeValueAsString(this);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    default PipelineStepData deserialize(String data) {
        var oom = createObjectMapper();
        try {
            return oom.readValue(data, this.getClass());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker() //
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY) //
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE) //
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE) //
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper;
    }
}
