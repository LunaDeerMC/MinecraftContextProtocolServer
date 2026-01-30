package cn.lunadeer.mc.mcp.api.command;

import cn.lunadeer.mc.mcp.MinecraftContextProtocolServer;
import cn.lunadeer.mc.mcp.core.registry.CapabilityDescriptor;
import cn.lunadeer.mc.mcp.core.registry.CapabilityRegistry;
import cn.lunadeer.mc.mcp.core.registry.ProviderDescriptor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

/**
 * Command to list all registered providers.
 * <p>
 * Displays information about all registered capability providers.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpProvidersCommand extends McpCommand {

    private final MinecraftContextProtocolServer plugin;

    public McpProvidersCommand(MinecraftContextProtocolServer plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "mcp.admin.providers")) {
            sendError(sender, "You don't have permission to use this command.");
            return false;
        }

        CapabilityRegistry registry = plugin.getCapabilityRegistry();
        Set<String> providerIds = registry.getProviderIds();

        if (providerIds.isEmpty()) {
            sendMessage(sender, "§7No providers registered.");
            return true;
        }

        sendMessage(sender, "§6=== Registered Providers (" + providerIds.size() + ") ===");

        for (String providerId : providerIds) {
            ProviderDescriptor descriptor = registry.getProviderDescriptor(providerId);
            if (descriptor != null) {
                sendMessage(sender, "§7- §f" + providerId + "§7 (v" + descriptor.getVersion() + ")");
                sendMessage(sender, "  §7Name: §f" + descriptor.getName());
                if (descriptor.getOwnerPlugin() != null) {
                    sendMessage(sender, "  §7Plugin: §f" + descriptor.getOwnerPlugin().getName());
                }
                sendMessage(sender, "  §7Capabilities: §f" + descriptor.getCapabilities().size());

                // List capability IDs
                for (CapabilityDescriptor cap : descriptor.getCapabilities()) {
                    sendMessage(sender, "    §7- §f" + cap.getId() + "§7 [" + cap.getType() + "]");
                }
            }
        }

        return true;
    }

    @Override
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return List.of();
    }
}
