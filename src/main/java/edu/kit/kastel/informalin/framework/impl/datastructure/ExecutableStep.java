package edu.kit.kastel.informalin.framework.impl.datastructure;

import java.util.List;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.IAgent;
import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;

public class ExecutableStep implements IExecutableStep {

    private ImmutableList<IAgent> agents;

    public ExecutableStep(List<IAgent> agents) {
        this.agents = Lists.immutable.withAll(agents);
    }

    @Override
    public ImmutableList<IAgent> getAgents() {
        return agents;
    }

    @Override
    public void init() {
        agents.forEach(a -> a.init());
    }

    @Override
    public void deinit() {
        agents.forEach(a -> a.deinit());
    }

}
