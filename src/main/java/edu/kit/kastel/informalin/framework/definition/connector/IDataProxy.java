package edu.kit.kastel.informalin.framework.definition.connector;

public interface IDataProxy {
    <D extends IDataProxy> D as(Class<D> target);
}
