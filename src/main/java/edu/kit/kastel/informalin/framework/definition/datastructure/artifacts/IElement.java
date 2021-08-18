package edu.kit.kastel.informalin.framework.definition.datastructure.artifacts;

public interface IElement {
    IArtifact getParent();

    String getID();

    <E extends IElement> E as(Class<E> target);
}
