package cn.lunadeer.mc.mcp.api;

import cn.lunadeer.mc.mcp.core.registry.CapabilityRegistry;
import cn.lunadeer.mc.mcp.sdk.api.McpEventEmitter;
import cn.lunadeer.mc.mcp.sdk.api.McpProviderRegistry;
import cn.lunadeer.mc.mcp.sdk.api.McpServer;

/**
 * Implementation of the McpServer interface.
 * <p>
 * Provides access to core MCP Server functionality including the provider registry
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
    private final String serverId;
    private boolean connected = false;

    /**
     * Constructs a new McpServerImpl.
     *
     * @param providerRegistry the provider registry
     * @param eventEmitter     the event emitter
     * @param version          the server version
     * @param serverId         the server ID
     */
    public McpServerImpl(
            CapabilityRegistry providerRegistry,
            McpEventEmitter eventEmitter,
            String version,
            String serverId
    ) {
        this.providerRegistry = providerRegistry;
        this.eventEmitter = eventEmitter;
        this.version = version;
        this.serverId = serverId;
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
    public String getServerId() {
        return serverId;
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
