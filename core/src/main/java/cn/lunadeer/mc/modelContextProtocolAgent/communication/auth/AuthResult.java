package cn.lunadeer.mc.modelContextProtocolAgent.communication.auth;

import java.util.Set;

/**
 * Result of an authentication attempt.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class AuthResult {
    private final boolean success;
    private final String reason;
    private final Set<String> permissions;

    private AuthResult(Builder builder) {
        this.success = builder.success;
        this.reason = builder.reason;
        this.permissions = builder.permissions;
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

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a successful authentication result.
     *
     * @param permissions the permissions granted
     * @return the authentication result
     */
    public static AuthResult success(Set<String> permissions) {
        return new Builder()
                .success(true)
                .permissions(permissions)
                .build();
    }

    /**
     * Creates a failed authentication result.
     *
     * @param reason the failure reason
     * @return the authentication result
     */
    public static AuthResult failure(String reason) {
        return new Builder()
                .success(false)
                .reason(reason)
                .build();
    }

    public static class Builder {
        private boolean success = true;
        private String reason;
        private Set<String> permissions;

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

        public AuthResult build() {
            return new AuthResult(this);
        }
    }
}
