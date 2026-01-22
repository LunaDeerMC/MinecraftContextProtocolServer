package cn.lunadeer.mc.modelContextProtocolAgent.api.command;

import cn.lunadeer.mc.modelContextProtocolAgent.ModelContextProtocolAgent;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.server.AgentWebSocketServer;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.GatewaySession;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.SessionManager;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

/**
 * Command to list all active sessions.
 * <p>
 * Displays information about all connected Gateway sessions.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpSessionsCommand extends McpCommand {

    private final ModelContextProtocolAgent plugin;

    public McpSessionsCommand(ModelContextProtocolAgent plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "mcp.admin.sessions")) {
            sendError(sender, "You don't have permission to use this command.");
            return false;
        }

        AgentWebSocketServer server = plugin.getWebSocketServer();
        if (server == null) {
            sendMessage(sender, "§7WebSocket server is not running.");
            return true;
        }

        SessionManager sessionManager = server.getSessionManager();
        Collection<GatewaySession> sessions = sessionManager.getAllSessions();

        if (sessions.isEmpty()) {
            sendMessage(sender, "§7No active sessions.");
            return true;
        }

        sendMessage(sender, "§6=== Active Sessions (" + sessions.size() + ") ===");

        for (GatewaySession session : sessions) {
            String status = session.isAuthenticated() ? "§aAuthenticated" : "§cUnauthenticated";
            sendMessage(sender, "§7- §f" + session.getId() + "§7 [" + status + "§7]");
            if (session.getGatewayId() != null) {
                sendMessage(sender, "  §7Gateway ID: §f" + session.getGatewayId());
            }
            if (session.getLastActivityAt() != null) {
                long seconds = java.time.Duration.between(
                        session.getLastActivityAt(), java.time.Instant.now()
                ).getSeconds();
                sendMessage(sender, "  §7Last Activity: §f" + seconds + "s ago");
            }
        }

        return true;
    }

    @Override
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return List.of();
    }
}
