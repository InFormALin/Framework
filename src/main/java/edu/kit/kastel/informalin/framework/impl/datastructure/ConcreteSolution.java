package edu.kit.kastel.informalin.framework.impl.datastructure;

import java.util.List;
import java.util.Objects;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;
import edu.kit.kastel.informalin.framework.definition.datastructure.ISolution;
import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataBlackboard;
import edu.kit.kastel.informalin.framework.definition.exec.IExecutionService;

public class ConcreteSolution implements ISolution {
    private ImmutableList<IExecutableStep> steps;
    private IExecutionService executor;

    public ConcreteSolution(List<IExecutableStep> steps, IExecutionService executor) {
        this.steps = Lists.immutable.withAll(steps);
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    public void invoke(IDataBlackboard blackboard) {
        steps.forEach(IExecutableStep::init);

        for (IExecutableStep step : steps) {
            executor.invoke(step, blackboard);
        }

        steps.forEach(IExecutableStep::deinit);
    }
}
