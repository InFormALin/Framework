package edu.kit.kastel.informalin.framework.definition.link;

import edu.kit.kastel.informalin.framework.definition.datastructure.artifacts.IElement;

public interface ILink {

    IElement getFormalElement();

    IElement getInformalElement();

    double getConfidence();
}
