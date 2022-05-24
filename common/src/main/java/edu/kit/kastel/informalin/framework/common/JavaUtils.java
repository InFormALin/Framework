/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public final class JavaUtils {

    private JavaUtils() {
        throw new IllegalAccessError();
    }

    public static <K, V> Map<K, V> copyMap(Map<K, V> map, UnaryOperator<V> copy) {
        Map<K, V> copyMap = new HashMap<>();
        for (var entry : map.entrySet()) {
            copyMap.put(entry.getKey(), copy.apply(entry.getValue()));
        }
        return copyMap;
    }

}
