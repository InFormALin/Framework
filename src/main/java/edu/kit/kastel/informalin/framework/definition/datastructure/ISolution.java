package edu.kit.kastel.informalin.framework.definition.datastructure;

import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;

public interface ISolution {
    void invoke(IDataBlackboard blackboard);
}
