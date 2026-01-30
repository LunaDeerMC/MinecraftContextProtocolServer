package cn.lunadeer.mc.mcp.communication.session;

/**
 * Statistics about sessions.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class SessionStats {
    private final int totalSessions;
    private final int authenticatedSessions;

    private SessionStats(Builder builder) {
        this.totalSessions = builder.totalSessions;
        this.authenticatedSessions = builder.authenticatedSessions;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public int getAuthenticatedSessions() {
        return authenticatedSessions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalSessions;
        private int authenticatedSessions;

        public Builder totalSessions(int totalSessions) {
            this.totalSessions = totalSessions;
            return this;
        }

        public Builder authenticatedSessions(int authenticatedSessions) {
            this.authenticatedSessions = authenticatedSessions;
            return this;
        }

        public SessionStats build() {
            return new SessionStats(this);
        }
    }
}
