package cn.lunadeer.mc.mcp.core.registry;

import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Descriptor for a registered MCP provider.
 * <p>
 * Contains metadata about a provider instance and all capabilities it provides.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class ProviderDescriptor {

    /**
     * Unique provider identifier (from @McpProvider annotation).
     */
    private final String id;

    /**
     * Display name of the provider.
     */
    private final String name;

    /**
     * Version of the provider.
     */
    private final String version;

    /**
     * Provider instance.
     */
    private final Object instance;

    /**
     * The plugin that owns this provider.
     */
    private final Plugin ownerPlugin;

    /**
     * List of capabilities provided by this provider.
     */
    private final List<CapabilityDescriptor> capabilities;

    /**
     * Constructs a new ProviderDescriptor.
     *
     * @param id           the provider ID
     * @param name         the provider name
     * @param version      the provider version
     * @param instance     the provider instance
     * @param ownerPlugin  the owning plugin
     * @param capabilities the capabilities provided
     */
    public ProviderDescriptor(
            String id,
            String name,
            String version,
            Object instance,
            Plugin ownerPlugin,
            List<CapabilityDescriptor> capabilities) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.instance = instance;
        this.ownerPlugin = ownerPlugin;
        this.capabilities = capabilities;
    }

    /**
     * Gets the unique provider identifier.
     *
     * @return the provider ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the display name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the provider instance.
     *
     * @return the instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Gets the owning plugin.
     *
     * @return the owner plugin
     */
    public Plugin getOwnerPlugin() {
        return ownerPlugin;
    }

    /**
     * Gets the capabilities provided by this provider.
     *
     * @return the capabilities list
     */
    public List<CapabilityDescriptor> getCapabilities() {
        return capabilities;
    }
}
