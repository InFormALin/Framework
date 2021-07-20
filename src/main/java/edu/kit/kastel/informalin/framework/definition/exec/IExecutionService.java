package edu.kit.kastel.informalin.framework.definition.exec;

import edu.kit.kastel.informalin.framework.definition.connector.IDataProxy;
import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;

public interface IExecutionService {
    void invoke(IExecutableStep step, IDataProxy blackboard);
}
