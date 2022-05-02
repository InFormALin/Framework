/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.pcm;

import org.fuchss.xmlobjectmapper.XML2Object;

import java.io.*;

public class PCMModel {
    private PCMRepository repository;

    public PCMModel(File repository) throws IOException {
        this(new FileInputStream(repository));
    }

    public PCMModel(InputStream repositoryStream) {
        try {
            this.load(repositoryStream);
        } catch (ReflectiveOperationException | IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public PCMRepository getRepository() {
        return repository;
    }

    private void load(InputStream repositoryStream) throws ReflectiveOperationException, IOException {
        XML2Object xml2Object = new XML2Object();
        xml2Object.registerClasses(PCMRepository.class, PCMComponent.class, PCMInterface.class, PCMSignature.class, PCMComponent.InterfaceId.class);
        repository = xml2Object.parseXML(repositoryStream, PCMRepository.class);
        repository.init();
    }
}
