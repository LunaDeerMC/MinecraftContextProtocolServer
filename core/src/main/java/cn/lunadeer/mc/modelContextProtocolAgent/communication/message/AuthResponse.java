package cn.lunadeer.mc.modelContextProtocolAgent.communication.message;

import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.CapabilityManifest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Set;

/**
 * Authentication response message.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class AuthResponse extends McpMessage {
    private final boolean success;
    private final String reason;
    private final Set<String> permissions;
    private final List<CapabilityManifest> capabilities;

    private AuthResponse(Builder builder) {
        super(builder.id, "auth_response");
        this.success = builder.success;
        this.reason = builder.reason;
        this.permissions = builder.permissions;
        this.capabilities = builder.capabilities;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public List<CapabilityManifest> getCapabilities() {
        return capabilities;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("success", success);
        if (reason != null) {
            payload.addProperty("reason", reason);
        }
        if (permissions != null && !permissions.isEmpty()) {
            JsonArray permsArray = new JsonArray();
            for (String perm : permissions) {
                permsArray.add(perm);
            }
            payload.add("permissions", permsArray);
        }
        if (capabilities != null && !capabilities.isEmpty()) {
            JsonArray capsArray = new JsonArray();
            Gson gson = new GsonBuilder().create();
            for (CapabilityManifest cap : capabilities) {
                capsArray.add(gson.toJsonTree(cap));
            }
            payload.add("capabilities", capsArray);
        }
        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private boolean success = true;
        private String reason;
        private Set<String> permissions;
        private List<CapabilityManifest> capabilities;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder permissions(Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder capabilities(List<CapabilityManifest> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(this);
        }
    }
}
