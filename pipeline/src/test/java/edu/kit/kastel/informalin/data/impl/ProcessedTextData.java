/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.data.impl;

import edu.kit.kastel.informalin.data.PipelineStepData;

import java.util.List;

/**
 * Example {@link PipelineStepData}
 * @author Jan Keim
 */
public class ProcessedTextData extends PipelineStepData {
    private List<String> importantTokens = null;

    public ProcessedTextData() {
        this.importantTokens = null;
    }

    public List<String> getImportantTokens() {
        return importantTokens;
    }

    public void setImportantTokens(List<String> importantTokens) {
        this.importantTokens = importantTokens;
    }
}
