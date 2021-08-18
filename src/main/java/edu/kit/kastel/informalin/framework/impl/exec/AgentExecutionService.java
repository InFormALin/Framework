package edu.kit.kastel.informalin.framework.impl.exec;

import java.util.Random;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.IAgent;
import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;
import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;
import edu.kit.kastel.informalin.framework.definition.exec.IExecutionService;

public class AgentExecutionService implements IExecutionService {

    public static final int DEFAULT_ROUNDS = 3;
    public static final boolean DEFAULT_SHUFFLE = false;

    // We want to produce reproducible results .. therefore fix random
    private Random random = new Random(42);

    private int rounds;
    private boolean shuffle;

    public AgentExecutionService(int rounds) {
        this(rounds, DEFAULT_SHUFFLE);
    }

    public AgentExecutionService(boolean shuffle) {
        this(DEFAULT_ROUNDS, shuffle);
    }

    public AgentExecutionService(int rounds, boolean shuffle) {
        this.rounds = rounds;
        this.shuffle = shuffle;
    }

    @Override
    public void invoke(IExecutableStep step, IDataBlackboard blackboard) {
        for (int round = 0; round < rounds; round++) {
            ImmutableList<IAgent> agents = shuffle ? shuffle(step.getAgents()) : step.getAgents();

            for (IAgent agent : agents) {
                agent.invoke(blackboard);
            }
        }
    }

    private ImmutableList<IAgent> shuffle(ImmutableList<IAgent> agents) {
        MutableList<IAgent> modifiable = agents.toList();
        modifiable.shuffleThis(random);
        return modifiable.toImmutable();
    }

}
