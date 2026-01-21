package cn.lunadeer.mc.modelContextProtocolAgent.communication.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;

/**
 * Heartbeat message for connection health monitoring.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class HeartbeatMessage extends McpMessage {
    private final String agentId;
    private final Instant timestamp;
    private final AgentStatus status;

    private HeartbeatMessage(Builder builder) {
        super(builder.id, "heartbeat");
        this.agentId = builder.agentId;
        this.timestamp = builder.timestamp;
        this.status = builder.status;
    }

    public String getAgentId() {
        return agentId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public AgentStatus getStatus() {
        return status;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("agentId", agentId);
        payload.addProperty("timestamp", timestamp.toString());
        if (status != null) {
            JsonObject statusObj = new JsonObject();
            statusObj.addProperty("healthy", status.isHealthy());
            statusObj.addProperty("tps", status.getTps());
            statusObj.addProperty("onlinePlayers", status.getOnlinePlayers());
            statusObj.addProperty("memoryUsage", status.getMemoryUsage());
            statusObj.addProperty("connectedGateways", status.getConnectedGateways());
            payload.add("status", statusObj);
        }
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String agentId;
        private Instant timestamp;
        private AgentStatus status;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder agentId(String agentId) {
            this.agentId = agentId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(AgentStatus status) {
            this.status = status;
            return this;
        }

        public HeartbeatMessage build() {
            return new HeartbeatMessage(this);
        }
    }
}
