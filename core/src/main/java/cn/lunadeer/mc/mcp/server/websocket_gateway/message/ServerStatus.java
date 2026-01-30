package cn.lunadeer.mc.mcp.server.websocket_gateway.message;

/**
 * Server status information for heartbeat messages.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class ServerStatus {
    private final boolean healthy;
    private final double tps;
    private final int onlinePlayers;
    private final double memoryUsage;
    private final int connectedGateways;

    private ServerStatus(Builder builder) {
        this.healthy = builder.healthy;
        this.tps = builder.tps;
        this.onlinePlayers = builder.onlinePlayers;
        this.memoryUsage = builder.memoryUsage;
        this.connectedGateways = builder.connectedGateways;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public double getTps() {
        return tps;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public int getConnectedGateways() {
        return connectedGateways;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean healthy = true;
        private double tps = 20.0;
        private int onlinePlayers = 0;
        private double memoryUsage = 0.0;
        private int connectedGateways = 0;

        public Builder healthy(boolean healthy) {
            this.healthy = healthy;
            return this;
        }

        public Builder tps(double tps) {
            this.tps = tps;
            return this;
        }

        public Builder onlinePlayers(int onlinePlayers) {
            this.onlinePlayers = onlinePlayers;
            return this;
        }

        public Builder memoryUsage(double memoryUsage) {
            this.memoryUsage = memoryUsage;
            return this;
        }

        public Builder connectedGateways(int connectedGateways) {
            this.connectedGateways = connectedGateways;
            return this;
        }

        public ServerStatus build() {
            return new ServerStatus(this);
        }
    }
}
