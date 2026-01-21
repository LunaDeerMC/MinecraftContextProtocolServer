package cn.lunadeer.mc.modelContextProtocolAgent.communication.message;

import com.google.gson.JsonElement;

/**
 * Base class for all MCP protocol messages.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public abstract class McpMessage {
    private final String id;
    private final String type;
    private String sessionId;
    private String gatewayId;
    private String correlationId;

    protected McpMessage(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Get the payload of this message.
     * @return the payload as JsonElement
     */
    public abstract JsonElement getPayload();
}
