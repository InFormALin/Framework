/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.pcm;

import java.util.ArrayList;
import java.util.List;

import org.fuchss.xmlobjectmapper.annotation.XMLClass;
import org.fuchss.xmlobjectmapper.annotation.XMLList;
import org.fuchss.xmlobjectmapper.annotation.XMLValue;

@XMLClass(name = "repository:Repository")
public final class PCMRepository {
    @XMLValue
    private String id;
    @XMLValue(mandatory = false)
    private String entityName;
    @XMLList(name = "components__Repository", elementType = PCMComponent.class)
    private List<PCMComponent> components;

    public PCMRepository() {
        // NOP
    }

    public String getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }

    public List<PCMComponent> getComponents() {
        return new ArrayList<>(components);
    }
}
