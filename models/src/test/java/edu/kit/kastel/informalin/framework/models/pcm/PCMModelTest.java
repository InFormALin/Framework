/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.pcm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PCMModelTest {
    private static final String PATH_TO_MODEL = "src/test/resources/benchmark/mediastore/original_model/ms.repository";

    @Test
    void simpleLoad() {
        PCMModel pcmModel = new PCMModel(new File(PATH_TO_MODEL));
        Assertions.assertNotNull(pcmModel.getRepository());
        var repo = pcmModel.getRepository();
        for (var component : repo.getComponents()) {
            Assertions.assertFalse(component.getProvided().isEmpty(), "Component " + component.getEntityName() + " has no provided interface");
        }
    }
}
