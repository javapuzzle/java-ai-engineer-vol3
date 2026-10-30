package com.acme.retail.agents.ch12;

/**
 * What {@link McpClient} actually gives its own callers -- never a raw
 * exception, and never an unchecked pass-through of whatever the server
 * returned. Success carries a real value; failure carries a reason,
 * whether that reason came from a thrown exception, a connection
 * problem, or a response the client itself decided not to trust.
 */
public record McpClientResult(boolean success, String value, String errorMessage) {

    public static McpClientResult success(String value) {
        return new McpClientResult(true, value, null);
    }

    public static McpClientResult failure(String errorMessage) {
        return new McpClientResult(false, null, errorMessage);
    }
}
