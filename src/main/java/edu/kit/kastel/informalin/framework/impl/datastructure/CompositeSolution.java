package edu.kit.kastel.informalin.framework.impl.datastructure;

import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.ISolution;

public class CompositeSolution implements ISolution {
    private ImmutableList<ISolution> solutions;
}
