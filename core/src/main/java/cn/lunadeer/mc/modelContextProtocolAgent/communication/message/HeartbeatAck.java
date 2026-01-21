package cn.lunadeer.mc.modelContextProtocolAgent.communication.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;

/**
 * Heartbeat acknowledgment message.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class HeartbeatAck extends McpMessage {
    private final String agentId;
    private final Instant timestamp;

    private HeartbeatAck(Builder builder) {
        super(builder.id, "heartbeat_ack");
        this.agentId = builder.agentId;
        this.timestamp = builder.timestamp;
    }

    public String getAgentId() {
        return agentId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("agentId", agentId);
        payload.addProperty("timestamp", timestamp.toString());
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String agentId;
        private Instant timestamp;

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

        public HeartbeatAck build() {
            return new HeartbeatAck(this);
        }
    }
}
