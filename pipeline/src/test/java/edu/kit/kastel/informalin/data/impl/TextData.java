/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.data.impl;

import edu.kit.kastel.informalin.data.PipelineStepData;

import java.util.List;
import java.util.Objects;

/**
 * Example {@link PipelineStepData}
 * 
 * @author Jan Keim
 */
public class TextData implements PipelineStepData {
    private final String text;
    private List<String> tokens;

    public TextData(String text) {
        this.text = text;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TextData textData = (TextData) o;

        return Objects.equals(text, textData.text);
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}
