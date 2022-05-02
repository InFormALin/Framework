/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.pcm;

import org.fuchss.xmlobjectmapper.XML2Object;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class PCMModel {
    private final File repositoryFile;

    private PCMRepository repository;

    public PCMModel(File repository) {
        this.repositoryFile = Objects.requireNonNull(repository);
        try {
            this.load();
        } catch (ReflectiveOperationException | IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public PCMRepository getRepository() {
        return repository;
    }

    private void load() throws ReflectiveOperationException, IOException {
        XML2Object xml2Object = new XML2Object();
        xml2Object.registerClasses(PCMRepository.class, PCMComponent.class, PCMInterface.class, PCMSignature.class, PCMComponent.InterfaceId.class);
        try (var repoStream = new FileInputStream(this.repositoryFile)) {
            repository = xml2Object.parseXML(repoStream, PCMRepository.class);
            repository.init();
        }
    }
}
