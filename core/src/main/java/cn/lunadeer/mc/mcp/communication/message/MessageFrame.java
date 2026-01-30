package cn.lunadeer.mc.mcp.communication.message;

import com.google.gson.JsonElement;

import java.time.Instant;

/**
 * Message frame that wraps MCP messages with metadata.
 * This is the outer structure for all WebSocket messages.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class MessageFrame {
    private final String id;
    private final String type;
    private final Instant timestamp;
    private final String correlationId;
    private final JsonElement payload;

    private MessageFrame(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.timestamp = builder.timestamp;
        this.correlationId = builder.correlationId;
        this.payload = builder.payload;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public JsonElement getPayload() {
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String type;
        private Instant timestamp;
        private String correlationId;
        private JsonElement payload;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder payload(JsonElement payload) {
            this.payload = payload;
            return this;
        }

        public MessageFrame build() {
            return new MessageFrame(this);
        }
    }
}
