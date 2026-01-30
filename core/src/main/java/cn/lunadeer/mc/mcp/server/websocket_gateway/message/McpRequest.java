package cn.lunadeer.mc.mcp.server.websocket_gateway.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP request message for invoking capabilities.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpRequest extends McpMessage {
    private final String capabilityId;
    private final Map<String, Object> parameters;
    private final String callerId;

    private McpRequest(Builder builder) {
        super(builder.id, "request");
        this.capabilityId = builder.capabilityId;
        this.parameters = builder.parameters;
        this.callerId = builder.callerId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getCallerId() {
        return callerId;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("capabilityId", capabilityId);
        payload.addProperty("callerId", callerId);
        if (parameters != null && !parameters.isEmpty()) {
            JsonObject params = new JsonObject();
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (entry.getValue() instanceof String) {
                    params.addProperty(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof Number) {
                    params.addProperty(entry.getKey(), (Number) entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    params.addProperty(entry.getKey(), (Boolean) entry.getValue());
                }
            }
            payload.add("parameters", params);
        }
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String capabilityId;
        private Map<String, Object> parameters = new HashMap<>();
        private String callerId;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder capabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
            return this;
        }

        public Builder parameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }

        public Builder callerId(String callerId) {
            this.callerId = callerId;
            return this;
        }

        public McpRequest build() {
            return new McpRequest(this);
        }
    }
}
