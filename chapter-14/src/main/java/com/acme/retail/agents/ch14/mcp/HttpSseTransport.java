package com.acme.retail.agents.ch14.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HTTP+SSE transport: the client and server can run on different
 * machines, which means every request and response actually crosses a
 * real serialization boundary -- exactly what this class performs with
 * Jackson, rather than merely naming it. The request is serialized to
 * JSON and deserialized back into a brand-new object before the server
 * ever sees it, and the same happens to the result on the way back --
 * modeling the real cost (and the real decoupling) an HTTP+SSE deployment
 * actually has that stdio does not.
 */
public class HttpSseTransport implements McpTransport {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public McpToolCallResult callTool(McpServer server, McpToolCallRequest request) {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            McpToolCallRequest deserializedRequest = objectMapper.readValue(requestJson, McpToolCallRequest.class);

            McpToolCallResult result = server.callTool(deserializedRequest);

            String resultJson = objectMapper.writeValueAsString(result);
            return objectMapper.readValue(resultJson, McpToolCallResult.class);
        } catch (Exception e) {
            return McpToolCallResult.error("Transport-level failure: " + e.getMessage());
        }
    }
}
