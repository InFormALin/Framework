package edu.kit.kastel.informalin.framework.impl.datastructure;

import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.IExecutableStep;
import edu.kit.kastel.informalin.framework.definition.datastructure.ISolution;
import edu.kit.kastel.informalin.framework.definition.exec.IExecutionService;

public class ConcreteSolution implements ISolution {
    private ImmutableList<IExecutableStep> steps;
    private IExecutionService executor;
}
