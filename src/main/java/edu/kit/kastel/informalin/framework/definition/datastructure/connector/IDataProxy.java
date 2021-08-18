package edu.kit.kastel.informalin.framework.definition.datastructure.connector;

public interface IDataProxy {
    /**
     * Cast your data proxy to a specific data proxy.
     *
     * @param <D>    the target type
     * @param target the target class
     * @return this data proxy as target or null iff not possible
     */
    <D extends IDataProxy> D as(Class<D> target);
}
