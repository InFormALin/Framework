/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.uml.xml_elements;

import java.util.List;
import java.util.Objects;

import org.fuchss.xmlobjectmapper.annotation.XMLClass;
import org.fuchss.xmlobjectmapper.annotation.XMLList;
import org.fuchss.xmlobjectmapper.annotation.XMLValue;

@XMLClass
public final class PackagedElement {
    @XMLValue(name = "xmi:id")
    private String id;
    @XMLValue(name = "xmi:type")
    private String type;
    @XMLValue(mandatory = false)
    private String name;

    @XMLList(name = "ownedOperation", elementType = OwnedOperation.class)
    private List<OwnedOperation> ownedOperations;

    @XMLList(name = "interfaceRealization", elementType = InterfaceRealization.class)
    private List<InterfaceRealization> interfaceRealizations;

    @XMLList(name = "packagedElement", elementType = Usage.class)
    private List<Usage> usages;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isComponent() {
        return Objects.equals(type, "uml:Component");
    }

    public boolean isInterface() {
        return Objects.equals(type, "uml:Interface");
    }

    public List<OwnedOperation> getOwnedOperations() {
        return ownedOperations;
    }

    public List<InterfaceRealization> getInterfaceRealizations() {
        return interfaceRealizations;
    }

    public List<Usage> getUsages() {
        return usages;
    }
}
