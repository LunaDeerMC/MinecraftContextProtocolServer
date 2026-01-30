package cn.lunadeer.mc.mcp.core.execution;

import java.util.Set;

/**
 * Information about the caller of a capability.
 * <p>
 * Contains permissions, roles, and other identity information
 * needed for authorization and auditing.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class CallerInfo {

    /**
     * Unique identifier for the caller (gateway ID).
     */
    private final String id;

    /**
     * Display name for the caller.
     */
    private final String name;

    /**
     * Set of permissions the caller has.
     */
    private final Set<String> permissions;

    /**
     * Set of roles the caller has.
     */
    private final Set<String> roles;

    /**
     * Constructs a new CallerInfo.
     *
     * @param id          the caller ID
     * @param name        the caller name
     * @param permissions the caller's permissions
     * @param roles       the caller's roles
     */
    public CallerInfo(String id, String name, Set<String> permissions, Set<String> roles) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
        this.roles = roles;
    }

    /**
     * Gets the caller ID.
     *
     * @return the caller ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the caller name.
     *
     * @return the caller name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the caller's permissions.
     *
     * @return the permissions set
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Gets the caller's roles.
     *
     * @return the roles set
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Checks if the caller has a specific permission.
     *
     * @param permission the permission to check
     * @return true if the caller has the permission
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    /**
     * Checks if the caller has a specific role.
     *
     * @param role the role to check
     * @return true if the caller has the role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Checks if the caller has any of the required permissions.
     *
     * @param requiredPermissions the required permissions
     * @return true if the caller has at least one permission
     */
    public boolean hasAnyPermission(Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true;
        }
        if (permissions == null) {
            return false;
        }
        return requiredPermissions.stream().anyMatch(permissions::contains);
    }

    /**
     * Checks if the caller has all required permissions.
     *
     * @param requiredPermissions the required permissions
     * @return true if the caller has all permissions
     */
    public boolean hasAllPermissions(Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true;
        }
        if (permissions == null) {
            return false;
        }
        return requiredPermissions.stream().allMatch(permissions::contains);
    }
}
