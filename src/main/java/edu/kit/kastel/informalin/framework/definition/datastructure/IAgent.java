package edu.kit.kastel.informalin.framework.definition.datastructure;

import edu.kit.kastel.informalin.framework.definition.connector.IDataProxy;

public interface IAgent {

    void invoke(IDataProxy blackboard);

}
