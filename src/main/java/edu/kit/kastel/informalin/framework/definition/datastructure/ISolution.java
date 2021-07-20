package edu.kit.kastel.informalin.framework.definition.datastructure;

import edu.kit.kastel.informalin.framework.definition.connector.IDataProxy;

public interface ISolution {
    void invoke(IDataProxy blackboard);
}
