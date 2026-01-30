package cn.lunadeer.mc.mcp.server.websocket_gateway.message;

import com.google.gson.*;

/**
 * MCP event message for real-time event notifications.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpEvent extends McpMessage {
    private final String eventId;
    private final Object eventData;

    private McpEvent(Builder builder) {
        super(builder.id, "event");
        this.eventId = builder.eventId;
        this.eventData = builder.eventData;
    }

    public String getEventId() {
        return eventId;
    }

    public Object getEventData() {
        return eventData;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("eventId", eventId);
        if (eventData != null) {
            Gson gson = new GsonBuilder().create();
            payload.add("eventData", JsonParser.parseString(gson.toJson(eventData)));
        }
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String eventId;
        private Object eventData;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventData(Object eventData) {
            this.eventData = eventData;
            return this;
        }

        public McpEvent build() {
            return new McpEvent(this);
        }
    }
}
