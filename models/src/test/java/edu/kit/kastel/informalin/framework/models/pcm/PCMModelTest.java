/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.pcm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class PCMModelTest {
    // TODO provide the model!
    private static final String PATH_TO_MODEL = "src/test/resources/benchmark/mediastore/original_model/ms.repository";

    @Disabled("Disabled for now")
    @Test
    void simpleLoad() throws IOException {
        PCMModel pcmModel = new PCMModel(new File(PATH_TO_MODEL));
        Assertions.assertNotNull(pcmModel.getRepository());
        var repo = pcmModel.getRepository();
        for (var component : repo.getComponents()) {
            Assertions.assertFalse(component.getProvided().isEmpty(), "Component " + component.getEntityName() + " has no provided interface");
        }
    }
}
