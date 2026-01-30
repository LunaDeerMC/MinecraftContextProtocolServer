package cn.lunadeer.mc.mcp.api.command;

import cn.lunadeer.mc.mcp.MinecraftContextProtocolServer;
import cn.lunadeer.mc.mcp.core.registry.CapabilityRegistry;
import cn.lunadeer.mc.mcp.sdk.model.CapabilityManifest;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to list all registered capabilities.
 * <p>
 * Displays information about all available MCP capabilities.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpCapabilitiesCommand extends McpCommand {

    private final MinecraftContextProtocolServer plugin;

    public McpCapabilitiesCommand(MinecraftContextProtocolServer plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "mcp.admin.capabilities")) {
            sendError(sender, "You don't have permission to use this command.");
            return false;
        }

        CapabilityRegistry registry = plugin.getCapabilityRegistry();
        List<CapabilityManifest> capabilities = registry.getCapabilities();

        if (capabilities.isEmpty()) {
            sendMessage(sender, "§7No capabilities registered.");
            return true;
        }

        sendMessage(sender, "§6=== Registered Capabilities (" + capabilities.size() + ") ===");

        for (CapabilityManifest cap : capabilities) {
            sendMessage(sender, "§7- §f" + cap.getId() + "§7 [" + cap.getType() + "]");
            sendMessage(sender, "  §7Name: §f" + cap.getName());
            sendMessage(sender, "  §7Version: §f" + cap.getVersion());
            sendMessage(sender, "  §7Provider: §f" + cap.getProviderId());
            if (cap.getDescription() != null && !cap.getDescription().isEmpty()) {
                sendMessage(sender, "  §7Description: §f" + cap.getDescription());
            }
            if (!cap.getPermissions().isEmpty()) {
                sendMessage(sender, "  §7Permissions: §f" + String.join(", ", cap.getPermissions()));
            }
            if (cap.getRiskLevel() != null) {
                sendMessage(sender, "  §7Risk Level: §f" + cap.getRiskLevel());
            }
        }

        return true;
    }

    @Override
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return List.of();
    }
}
