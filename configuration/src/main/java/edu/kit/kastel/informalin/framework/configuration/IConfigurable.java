/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.configuration;

import java.util.Map;

public interface IConfigurable {
    void applyConfiguration(Map<String, String> additionalConfiguration);

    Map<String, String> getLastAppliedConfiguration();
}
