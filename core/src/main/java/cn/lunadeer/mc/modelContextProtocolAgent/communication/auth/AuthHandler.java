package cn.lunadeer.mc.modelContextProtocolAgent.communication.auth;

import cn.lunadeer.mc.modelContextProtocolAgent.Configuration;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.I18n;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.XLogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles authentication for Gateway connections.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class AuthHandler {

    /**
     * Authenticates a Gateway connection.
     *
     * @param gatewayId the gateway identifier
     * @param token the authentication token
     * @return the authentication result
     */
    public AuthResult authenticate(String gatewayId, String token) {
        // 1. Validate token
        if (!validateToken(token)) {
            XLogger.warn(I18n.communicationText.authFailedInvalidToken, gatewayId);
            return AuthResult.failure("Invalid token");
        }

        // 2. Check Gateway ID against whitelist (if enabled)
        if (Configuration.websocketServer.maxConnections > 0) {
            // Note: In a real implementation, we'd check against a configured whitelist
            // For now, we accept any valid token
            XLogger.debug(I18n.communicationText.gatewayPassedWhitelist, gatewayId);
        }

        // 3. Grant permissions based on configuration
        Set<String> permissions = getGatewayPermissions(gatewayId);

        XLogger.info(I18n.communicationText.gatewayAuthenticated, gatewayId, permissions.size());

        return AuthResult.success(permissions);
    }

    /**
     * Validates the authentication token.
     *
     * @param token the token to validate
     * @return true if valid, false otherwise
     */
    private boolean validateToken(String token) {
        String expectedToken = Configuration.websocketServer.authToken;

        // Allow empty token only in debug mode (for development)
        if (expectedToken == null || expectedToken.isBlank()) {
            XLogger.warn("Agent auth token is not configured, allowing all connections (development mode)");
            return true;
        }

        return expectedToken.equals(token);
    }

    /**
     * Gets the permissions for a Gateway.
     * In a real implementation, this would be configurable per Gateway.
     *
     * @param gatewayId the gateway identifier
     * @return the set of permissions
     */
    private Set<String> getGatewayPermissions(String gatewayId) {
        // For now, grant all permissions to authenticated Gateways
        // In production, this should be configurable per Gateway
        Set<String> permissions = new HashSet<>();
        permissions.add("mcp.*"); // Wildcard permission for all MCP operations
        return permissions;
    }
}
