package cn.lunadeer.mc.modelContextProtocolAgent.api.command;

import cn.lunadeer.mc.modelContextProtocolAgent.ModelContextProtocolAgent;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.server.AgentWebSocketServer;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.GatewaySession;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.SessionManager;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to kick a Gateway session.
 * <p>
 * Disconnects a specific Gateway session from the agent.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpKickCommand extends McpCommand {

    private final ModelContextProtocolAgent plugin;

    public McpKickCommand(ModelContextProtocolAgent plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "mcp.admin.sessions")) {
            sendError(sender, "You don't have permission to use this command.");
            return false;
        }

        if (args.length < 1) {
            sendError(sender, "Usage: /mcp kick <sessionId>");
            return false;
        }

        String sessionId = args[0];

        AgentWebSocketServer server = plugin.getWebSocketServer();
        if (server == null) {
            sendError(sender, "WebSocket server is not running.");
            return false;
        }

        SessionManager sessionManager = server.getSessionManager();
        GatewaySession session = sessionManager.getSession(sessionId);

        if (session == null) {
            sendError(sender, "Session not found: " + sessionId);
            return false;
        }

        session.close(1000, "Kicked by administrator");
        sessionManager.removeSession(sessionId);

        sendSuccess(sender, "Session " + sessionId + " has been kicked.");
        return true;
    }

    @Override
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 1) {
            AgentWebSocketServer server = plugin.getWebSocketServer();
            if (server != null) {
                SessionManager sessionManager = server.getSessionManager();
                return sessionManager.getAllSessions().stream()
                        .map(GatewaySession::getId)
                        .toList();
            }
        }
        return List.of();
    }
}
