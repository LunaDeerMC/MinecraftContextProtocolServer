package cn.lunadeer.mc.modelContextProtocolAgent.communication.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Authentication request message.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class AuthRequest extends McpMessage {
    private final String gatewayId;
    private final String token;

    private AuthRequest(Builder builder) {
        super(builder.id, "auth");
        this.gatewayId = builder.gatewayId;
        this.token = builder.token;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getToken() {
        return token;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("gatewayId", gatewayId);
        payload.addProperty("token", token);
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String gatewayId;
        private String token;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder gatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public AuthRequest build() {
            return new AuthRequest(this);
        }
    }
}
