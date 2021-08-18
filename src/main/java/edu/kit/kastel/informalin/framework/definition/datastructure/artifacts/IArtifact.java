package edu.kit.kastel.informalin.framework.definition.datastructure.artifacts;

import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.connector.IDataProxy;

public interface IArtifact {
    /**
     * Get a unique identifier of the artifact.
     *
     * @return the unique identifier
     */
    String getID();

    /**
     * Get human readable information about the artifact. <br>
     * Example: <em>Name: Description, Version N</em>
     *
     * @return a human readable string with (meta-)information about the artifact
     */
    String getDescription();

    <A extends IArtifact> A as(Class<A> target);

    /**
     * Get access to the data of the artifact. <br>
     * <code>getData().as(MySpecialData.class)</code>
     *
     * @return the data
     * @see IDataProxy#as(Class)
     */
    IDataProxy getData();

    /**
     * Get all elements from the artifacts.
     *
     * @return the elements
     */
    ImmutableList<IElement> getElements();
}
