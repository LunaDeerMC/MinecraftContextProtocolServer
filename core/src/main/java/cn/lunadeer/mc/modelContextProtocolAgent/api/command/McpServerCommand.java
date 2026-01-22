package cn.lunadeer.mc.modelContextProtocolAgent.api.command;

import cn.lunadeer.mc.modelContextProtocolAgent.ModelContextProtocolAgent;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to control the WebSocket server.
 * <p>
 * Starts or stops the WebSocket server.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpServerCommand extends McpCommand {

    private final ModelContextProtocolAgent plugin;

    public McpServerCommand(ModelContextProtocolAgent plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "mcp.admin.server")) {
            sendError(sender, "You don't have permission to use this command.");
            return false;
        }

        if (args.length < 1) {
            sendError(sender, "Usage: /mcp server <start|stop>");
            return false;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start":
                return startServer(sender);
            case "stop":
                return stopServer(sender);
            default:
                sendError(sender, "Unknown subcommand: " + subCommand);
                sendError(sender, "Usage: /mcp server <start|stop>");
                return false;
        }
    }

    private boolean startServer(CommandSender sender) {
        if (plugin.getWebSocketServer() != null) {
            sendError(sender, "WebSocket server is already running.");
            return false;
        }

        sendMessage(sender, "§6Starting WebSocket server...");
        try {
            plugin.startWebSocketServer();
            sendSuccess(sender, "WebSocket server started successfully!");
            return true;
        } catch (Exception e) {
            sendError(sender, "Failed to start WebSocket server: " + e.getMessage());
            return false;
        }
    }

    private boolean stopServer(CommandSender sender) {
        if (plugin.getWebSocketServer() == null) {
            sendError(sender, "WebSocket server is not running.");
            return false;
        }

        sendMessage(sender, "§6Stopping WebSocket server...");
        try {
            plugin.stopWebSocketServer();
            sendSuccess(sender, "WebSocket server stopped successfully!");
            return true;
        } catch (Exception e) {
            sendError(sender, "Failed to stop WebSocket server: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("start", "stop");
        }
        return List.of();
    }
}
