package cn.lunadeer.mc.mcp.api;

import cn.lunadeer.mc.mcp.core.registry.CapabilityRegistry;
import cn.lunadeer.mc.mcp.sdk.api.McpServer;
import cn.lunadeer.mc.mcp.sdk.api.McpEventEmitter;
import cn.lunadeer.mc.mcp.sdk.api.McpProviderRegistry;

/**
 * Implementation of the McpAgent interface.
 * <p>
 * Provides access to core MCP Agent functionality including the provider registry
 * and event emitter.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpServerImpl implements McpServer {

    private final CapabilityRegistry providerRegistry;
    private final McpEventEmitter eventEmitter;
    private final String version;
    private final String agentId;
    private boolean connected = false;

    /**
     * Constructs a new McpAgentImpl.
     *
     * @param providerRegistry the provider registry
     * @param eventEmitter     the event emitter
     * @param version          the agent version
     * @param agentId          the agent ID
     */
    public McpServerImpl(
            CapabilityRegistry providerRegistry,
            McpEventEmitter eventEmitter,
            String version,
            String agentId
    ) {
        this.providerRegistry = providerRegistry;
        this.eventEmitter = eventEmitter;
        this.version = version;
        this.agentId = agentId;
    }

    @Override
    public McpProviderRegistry getProviderRegistry() {
        return providerRegistry;
    }

    @Override
    public McpEventEmitter getEventEmitter() {
        return eventEmitter;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sets the connection status.
     *
     * @param connected true if connected
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    /**
     * Gets the capability registry (for internal use).
     *
     * @return the capability registry
     */
    public CapabilityRegistry getCapabilityRegistry() {
        return providerRegistry;
    }
}
