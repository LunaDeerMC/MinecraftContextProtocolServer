package cn.lunadeer.mc.modelContextProtocolAgent.core.permission;

import cn.lunadeer.mc.modelContextProtocolAgent.core.execution.CallerInfo;
import cn.lunadeer.mc.modelContextProtocolAgent.core.execution.ExecutionContext;
import cn.lunadeer.mc.modelContextProtocolAgent.core.execution.ExecutionInterceptor;
import cn.lunadeer.mc.modelContextProtocolAgent.core.registry.CapabilityDescriptor;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.XLogger;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.exception.McpSecurityException;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.ErrorCode;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.RiskLevel;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission Checker for MCP capabilities.
 * <p>
 * Validates that the caller has the required permissions and roles
 * to execute a capability. This interceptor runs early in the execution chain.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class PermissionChecker implements ExecutionInterceptor {

    private static final int ORDER = 100; // Early execution

    /**
     * Checks if the caller has permission to execute the capability.
     *
     * @param context the execution context
     * @return true to continue execution, false to skip
     * @throws McpSecurityException if permission check fails
     */
    @Override
    public boolean preHandle(ExecutionContext context) throws McpSecurityException {
        CapabilityDescriptor capability = context.getCapability();
        CallerInfo caller = context.getCaller();

        if (capability == null) {
            throw new McpSecurityException(
                    ErrorCode.PERMISSION_DENIED.getErrorCode(),
                    "No capability specified"
            );
        }

        if (caller == null) {
            throw new McpSecurityException(
                    ErrorCode.PERMISSION_DENIED.getErrorCode(),
                    "No caller information available"
            );
        }

        // Check permissions
        Set<String> requiredPermissions = new HashSet<>(capability.getPermissions());
        if (!requiredPermissions.isEmpty()) {
            if (!caller.hasAllPermissions(requiredPermissions)) {
                XLogger.debug("Permission denied for capability: " + capability.getId() +
                        ". Required: " + requiredPermissions +
                        ", Caller has: " + caller.getPermissions());

                throw new McpSecurityException(
                        ErrorCode.PERMISSION_DENIED.getErrorCode(),
                        "Insufficient permissions to execute capability: " + capability.getId()
                );
            }
        }

        // Check risk level requirements
        RiskLevel riskLevel = capability.getRiskLevel();
        if (riskLevel != null) {
            String requiredRole = getRequiredRoleForRiskLevel(riskLevel);
            if (requiredRole != null && !caller.hasRole(requiredRole)) {
                XLogger.debug("Role check failed for capability: " + capability.getId() +
                        ". Required role: " + requiredRole +
                        ", Caller roles: " + caller.getRoles());

                throw new McpSecurityException(
                        ErrorCode.PERMISSION_DENIED.getErrorCode(),
                        "Insufficient role to execute capability: " + capability.getId() +
                        ". Required role: " + requiredRole
                );
            }
        }

        XLogger.debug("Permission check passed for capability: " + capability.getId());
        return true;
    }

    @Override
    public void postHandle(ExecutionContext context, Object result) {
        // No post-processing needed
    }

    @Override
    public void onError(ExecutionContext context, Throwable ex) {
        // No error handling needed
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Gets the required role for a given risk level.
     *
     * @param riskLevel the risk level
     * @return the required role, or null if no role required
     */
    private String getRequiredRoleForRiskLevel(RiskLevel riskLevel) {
        switch (riskLevel) {
            case LOW:
                return null; // No role required for low risk
            case MEDIUM:
                return "operator";
            case HIGH:
                return "admin";
            case CRITICAL:
                return "super_admin";
            default:
                return null;
        }
    }
}
