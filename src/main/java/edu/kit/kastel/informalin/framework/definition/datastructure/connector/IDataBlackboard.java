package edu.kit.kastel.informalin.framework.definition.datastructure.connector;

import java.util.Objects;

import org.eclipse.collections.api.list.ImmutableList;

import edu.kit.kastel.informalin.framework.definition.datastructure.artifacts.IArtifact;
import edu.kit.kastel.informalin.framework.definition.link.ILink;

public interface IDataBlackboard {

    default IArtifact getArtifact(String id) {
        for (IArtifact artifact : getArtifacts()) {
            if (Objects.equals(artifact.getID(), id)) {
                return artifact;
            }
        }
        return null;
    }

    ImmutableList<IArtifact> getArtifacts();

    boolean addLink(ILink link);

    boolean deleteLink(ILink link);

    ImmutableList<ILink> getLinks();
}
