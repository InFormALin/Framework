package edu.kit.kastel.informalin.framework.impl.connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.apache.jena.ontology.OntModel;

import edu.kit.kastel.informalin.framework.definition.connector.IDataProxy;

public abstract class OntologyDataProxy implements IDataProxy {
    private OntModel ontology;

    protected OntologyDataProxy(OntModel ontology) {
        this.ontology = Objects.requireNonNull(ontology);
    }

    @Override
    public <D extends IDataProxy> D as(Class<D> target) {
        if (!getClass().isAssignableFrom(target)) {
            throw new IllegalArgumentException("Creation of " + target + " is not possible");
        }
        return this.generateSpecificOntologyDataProxy(target);
    }

    private <D extends IDataProxy> D generateSpecificOntologyDataProxy(Class<D> target) {
        try {
            Constructor<D> c = target.getDeclaredConstructor(OntModel.class);
            if (!Modifier.isPublic(c.getModifiers())) {
                throw new IllegalArgumentException(target + " has no public constuctor with ontology parameter");
            }
            return c.newInstance(this.ontology);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
