package edu.kit.kastel.informalin.framework.definition.exec;

import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;
import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;

public interface IExecutionService {
    void invoke(IExecutableStep step, IDataBlackboard blackboard);
}
