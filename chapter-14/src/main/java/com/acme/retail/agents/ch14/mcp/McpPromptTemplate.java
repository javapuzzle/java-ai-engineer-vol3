package com.acme.retail.agents.ch14.mcp;

import java.util.Map;

/**
 * A reusable prompt template -- rendered once per call with real
 * arguments, so every caller that needs "summarize this order" gets the
 * exact same wording instead of each team writing its own slightly
 * different version.
 */
public record McpPromptTemplate(String name, String template) {

    public String render(Map<String, String> arguments) {
        String rendered = template;
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (!rendered.contains(placeholder)) {
                throw new IllegalArgumentException("Template \"" + name + "\" has no placeholder named " + entry.getKey());
            }
            rendered = rendered.replace(placeholder, entry.getValue());
        }
        if (rendered.contains("{") && rendered.contains("}")) {
            throw new IllegalStateException("Template \"" + name + "\" still has unfilled placeholders: " + rendered);
        }
        return rendered;
    }
}
