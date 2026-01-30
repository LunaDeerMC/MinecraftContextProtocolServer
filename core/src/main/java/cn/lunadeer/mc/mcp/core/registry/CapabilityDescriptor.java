package cn.lunadeer.mc.mcp.core.registry;

import cn.lunadeer.mc.mcp.sdk.model.CapabilityManifest;
import cn.lunadeer.mc.mcp.sdk.model.CapabilityType;
import cn.lunadeer.mc.mcp.sdk.model.RiskLevel;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Descriptor for a registered MCP capability.
 * <p>
 * Contains all metadata needed to identify, validate, and execute a capability,
 * including the handler method, parameter schemas, and security requirements.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class CapabilityDescriptor {

    /**
     * Unique capability identifier (e.g., "world.time.get").
     */
    private final String id;

    /**
     * Version of the capability.
     */
    private final String version;

    /**
     * Type of capability (CONTEXT, ACTION, EVENT).
     */
    private final CapabilityType type;

    /**
     * Complete manifest containing all metadata.
     */
    private final CapabilityManifest manifest;

    /**
     * The provider instance that owns this capability.
     */
    private final Object providerInstance;

    /**
     * The method that handles this capability.
     */
    private final Method handlerMethod;

    /**
     * Parameter schema for validation (JSON Schema format).
     */
    private final java.util.Map<String, Object> parameterSchema;

    /**
     * Return value schema (JSON Schema format).
     */
    private final java.util.Map<String, Object> returnSchema;

    /**
     * Risk level for this capability (for ACTION types).
     */
    private final RiskLevel riskLevel;

    /**
     * List of required permissions.
     */
    private final List<String> permissions;

    /**
     * Whether rollback is supported (for ACTION types).
     */
    private final boolean rollbackSupported;

    /**
     * Whether a snapshot is required before execution.
     */
    private final boolean snapshotRequired;

    /**
     * Whether confirmation is required before execution.
     */
    private final boolean confirmRequired;

    /**
     * Whether results can be cached (for CONTEXT types).
     */
    private final boolean cacheable;

    /**
     * Cache TTL in seconds (for CONTEXT types).
     */
    private final int cacheTtl;

    /**
     * Tags for categorization.
     */
    private final List<String> tags;

    /**
     * Constructs a new CapabilityDescriptor.
     *
     * @param id                the capability ID
     * @param version           the capability version
     * @param type              the capability type
     * @param manifest          the capability manifest
     * @param providerInstance  the provider instance
     * @param handlerMethod     the handler method
     * @param parameterSchema   the parameter schema
     * @param returnSchema      the return schema
     * @param riskLevel         the risk level
     * @param permissions       the required permissions
     * @param rollbackSupported whether rollback is supported
     * @param snapshotRequired  whether snapshot is required
     * @param confirmRequired   whether confirmation is required
     * @param cacheable         whether cacheable
     * @param cacheTtl          the cache TTL
     * @param tags              the capability tags
     */
    public CapabilityDescriptor(
            String id,
            String version,
            CapabilityType type,
            CapabilityManifest manifest,
            Object providerInstance,
            Method handlerMethod,
            java.util.Map<String, Object> parameterSchema,
            java.util.Map<String, Object> returnSchema,
            RiskLevel riskLevel,
            List<String> permissions,
            boolean rollbackSupported,
            boolean snapshotRequired,
            boolean confirmRequired,
            boolean cacheable,
            int cacheTtl,
            List<String> tags) {
        this.id = id;
        this.version = version;
        this.type = type;
        this.manifest = manifest;
        this.providerInstance = providerInstance;
        this.handlerMethod = handlerMethod;
        this.parameterSchema = parameterSchema;
        this.returnSchema = returnSchema;
        this.riskLevel = riskLevel;
        this.permissions = permissions;
        this.rollbackSupported = rollbackSupported;
        this.snapshotRequired = snapshotRequired;
        this.confirmRequired = confirmRequired;
        this.cacheable = cacheable;
        this.cacheTtl = cacheTtl;
        this.tags = tags;
    }

    /**
     * Gets the unique capability identifier.
     *
     * @return the capability ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the capability version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the capability type.
     *
     * @return the capability type
     */
    public CapabilityType getType() {
        return type;
    }

    /**
     * Gets the capability manifest.
     *
     * @return the manifest
     */
    public CapabilityManifest getManifest() {
        return manifest;
    }

    /**
     * Gets the provider instance that owns this capability.
     *
     * @return the provider instance
     */
    public Object getProviderInstance() {
        return providerInstance;
    }

    /**
     * Gets the handler method.
     *
     * @return the handler method
     */
    public Method getHandlerMethod() {
        return handlerMethod;
    }

    /**
     * Gets the parameter schema.
     *
     * @return the parameter schema
     */
    public java.util.Map<String, Object> getParameterSchema() {
        return parameterSchema;
    }

    /**
     * Gets the return schema.
     *
     * @return the return schema
     */
    public java.util.Map<String, Object> getReturnSchema() {
        return returnSchema;
    }

    /**
     * Gets the risk level.
     *
     * @return the risk level
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * Gets the required permissions.
     *
     * @return the permissions list
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * Checks if rollback is supported.
     *
     * @return true if rollback is supported
     */
    public boolean isRollbackSupported() {
        return rollbackSupported;
    }

    /**
     * Checks if a snapshot is required before execution.
     *
     * @return true if snapshot is required
     */
    public boolean isSnapshotRequired() {
        return snapshotRequired;
    }

    /**
     * Checks if confirmation is required before execution.
     *
     * @return true if confirmation is required
     */
    public boolean isConfirmRequired() {
        return confirmRequired;
    }

    /**
     * Checks if results can be cached.
     *
     * @return true if cacheable
     */
    public boolean isCacheable() {
        return cacheable;
    }

    /**
     * Gets the cache TTL in seconds.
     *
     * @return cache TTL
     */
    public int getCacheTtl() {
        return cacheTtl;
    }

    /**
     * Gets the capability tags.
     *
     * @return the tags list
     */
    public List<String> getTags() {
        return tags;
    }
}
