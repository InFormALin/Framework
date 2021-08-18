package edu.kit.kastel.informalin.framework.definition.datastructure;

import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;

public interface IAgent {

    void invoke(IDataBlackboard blackboard);

    void init();

    void deinit();
}
