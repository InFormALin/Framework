package edu.kit.kastel.informalin.framework.impl.datastructure;

import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.IAgent;
import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;
import edu.kit.kastel.informalin.framework.definition.datastructure.ISolution;

public class SolutionExecutableStep implements IExecutableStep {
    private ISolution solution;

    @Override
    public ImmutableList<IAgent> getAgents() {
        // TODO how to get agents from solutions? shall we define a wrapper agent?
        throw new UnsupportedOperationException("NIY");
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("NIY");
    }

    @Override
    public void deinit() {
        throw new UnsupportedOperationException("NIY");
    }
}
