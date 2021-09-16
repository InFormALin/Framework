package edu.kit.kastel.informalin.framework.definition.datastructure;

import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;

public interface ISolution {

    boolean canInvoke(IDataBlackboard blackboard);

    void invoke(IDataBlackboard blackboard);
}
