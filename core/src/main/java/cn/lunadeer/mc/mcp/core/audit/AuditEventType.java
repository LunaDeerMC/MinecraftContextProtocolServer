package cn.lunadeer.mc.mcp.core.audit;

/**
 * Types of audit events.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public enum AuditEventType {

    /**
     * Capability invocation event.
     */
    INVOKE,

    /**
     * Capability execution completed successfully.
     */
    COMPLETED,

    /**
     * Capability execution failed.
     */
    FAILED,

    /**
     * Permission denied event.
     */
    PERMISSION_DENIED,

    /**
     * Rate limit exceeded event.
     */
    RATE_LIMIT_EXCEEDED,

    /**
     * Snapshot created event.
     */
    SNAPSHOT_CREATED,

    /**
     * Rollback executed event.
     */
    ROLLBACK_EXECUTED,

    /**
     * Provider registered event.
     */
    PROVIDER_REGISTERED,

    /**
     * Provider unregistered event.
     */
    PROVIDER_UNREGISTERED
}
