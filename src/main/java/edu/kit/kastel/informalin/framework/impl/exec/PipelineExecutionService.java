package edu.kit.kastel.informalin.framework.impl.exec;

import edu.kit.kastel.informalin.framework.definition.datastructure.IAgent;
import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;
import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;
import edu.kit.kastel.informalin.framework.definition.exec.IExecutionService;

public class PipelineExecutionService implements IExecutionService {

    @Override
    public void invoke(IExecutableStep step, IDataBlackboard blackboard) {
        for (IAgent agent : step.getAgents()) {
            agent.invoke(blackboard);
        }
    }
}
