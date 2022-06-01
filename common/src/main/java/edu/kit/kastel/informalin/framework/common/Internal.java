/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.common;

import java.lang.annotation.*;

/**
 * Marks a method or class as only for internal use. Classes and methods that are marked with this annotation are
 * subject of change.
 * 
 * @author Dominik Fuchss
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Internal {
}
