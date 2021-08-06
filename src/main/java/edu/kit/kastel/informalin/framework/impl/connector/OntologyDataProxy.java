package edu.kit.kastel.informalin.framework.impl.connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;

import edu.kit.kastel.informalin.framework.definition.connector.IDataProxy;
import edu.kit.kastel.informalin.ontology.OntologyConnector;

public abstract class OntologyDataProxy implements IDataProxy {
    private OntologyConnector ontology;

    protected OntologyDataProxy(OntologyConnector ontology) {
        this.ontology = Objects.requireNonNull(ontology);
    }

    @Override
    public <D extends IDataProxy> D as(Class<D> target) {
        if (!OntologyDataProxy.class.isAssignableFrom(target)) {
            throw new IllegalArgumentException("Creation of " + target + " is not possible");
        }
        return this.generateSpecificOntologyDataProxy(target);
    }

    private <D extends IDataProxy> D generateSpecificOntologyDataProxy(Class<D> target) {
        try {
            Constructor<D> c = target.getDeclaredConstructor(OntologyConnector.class);
            if (!Modifier.isPublic(c.getModifiers())) {
                throw new IllegalArgumentException(target + " has no public constuctor with OntologyConnector parameter");
            }
            return c.newInstance(ontology);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
