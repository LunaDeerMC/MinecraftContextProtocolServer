package cn.lunadeer.mc.mcp.api.command;

import cn.lunadeer.mc.mcp.MinecraftContextProtocolServer;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to reload MCP Agent configuration.
 * <p>
 * Reloads the configuration file and restarts the WebSocket server if needed.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpReloadCommand extends McpCommand {

    private final MinecraftContextProtocolServer plugin;

    public McpReloadCommand(MinecraftContextProtocolServer plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "mcp.admin.reload")) {
            sendError(sender, "You don't have permission to use this command.");
            return false;
        }

        sendMessage(sender, "§6Reloading MCP Agent configuration...");

        try {
            // Stop the WebSocket server
            if (plugin.getWebSocketServer() != null) {
                plugin.getWebSocketServer().stop();
            }

            // Reload configuration
            plugin.loadConfiguration();

            // Restart WebSocket server
            plugin.startWebSocketServer();

            sendSuccess(sender, "Configuration reloaded successfully!");
            return true;
        } catch (Exception e) {
            sendError(sender, "Failed to reload configuration: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return List.of();
    }
}
