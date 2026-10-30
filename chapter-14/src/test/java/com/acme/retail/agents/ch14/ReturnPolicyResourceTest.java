package com.acme.retail.agents.ch14;

import com.acme.retail.agents.ch14.mcp.McpResource;
import com.acme.retail.agents.ch14.mcp.McpResourceDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnPolicyResourceTest {

    private final AcmeMcpServer server = new AcmeMcpServer(
            new AcmeOrderTools(new OrderDirectory()),
            new AcmeInventoryTools(new InventoryDirectory()),
            new ReturnPolicyDocument());

    @Test
    void listResourcesDescribesTheReturnPolicyDocument() {
        List<McpResourceDescriptor> resources = server.listResources();

        assertThat(resources).hasSize(1);
        assertThat(resources.get(0).uri()).isEqualTo("acme://policies/returns");
        assertThat(resources.get(0).mimeType()).isEqualTo("text/plain");
    }

    @Test
    void readResourceReturnsTheActualPolicyText() {
        McpResource resource = server.readResource("acme://policies/returns");

        assertThat(resource.content()).contains("30 days");
    }

    @Test
    void readResourceWithAnUnknownUriThrows() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> server.readResource("acme://policies/shipping"));
    }
}
