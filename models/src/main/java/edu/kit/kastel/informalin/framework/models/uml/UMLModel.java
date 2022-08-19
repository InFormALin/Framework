/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.uml;

import java.io.*;

import org.fuchss.xmlobjectmapper.XML2Object;

import edu.kit.kastel.informalin.framework.models.uml.xml_elements.InterfaceRealization;
import edu.kit.kastel.informalin.framework.models.uml.xml_elements.OwnedOperation;
import edu.kit.kastel.informalin.framework.models.uml.xml_elements.PackagedElement;
import edu.kit.kastel.informalin.framework.models.uml.xml_elements.Usage;

public class UMLModel {
    private UMLModelRoot model;

    public UMLModel(File umlModel) throws FileNotFoundException {
        this(new FileInputStream(umlModel));
    }

    public UMLModel(InputStream umlModelStream) {
        try {
            this.load(umlModelStream);
        } catch (ReflectiveOperationException | IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public UMLModelRoot getModel() {
        return model;
    }

    private void load(InputStream repositoryStream) throws ReflectiveOperationException, IOException {
        XML2Object xml2Object = new XML2Object();
        xml2Object.registerClasses(UMLModelRoot.class, PackagedElement.class, OwnedOperation.class, InterfaceRealization.class, Usage.class);
        model = xml2Object.parseXML(repositoryStream, UMLModelRoot.class);
        model.init();
    }
}
