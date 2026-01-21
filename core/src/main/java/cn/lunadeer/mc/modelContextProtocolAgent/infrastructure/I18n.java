package cn.lunadeer.mc.modelContextProtocolAgent.infrastructure;

import cn.lunadeer.mc.modelContextProtocolAgent.ModelContextProtocolAgent;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.configuration.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Headers({
        "Language file for this plugin",
        "",
        "Most of the text support color codes,",
        "you can use §0-§9 for colors, §l for bold, §o for italic, §n for underline, §m for strikethrough, and §k for magic.",
        "Also support '&' as an alternative for '§'.",
})
public class I18n extends ConfigurationFile {

    // languages file name list here will be saved to plugin data folder
    @HandleManually
    public enum LanguageCode {
        en_US,
        zh_CN,
    }

    public static class ConfigurationText extends ConfigurationPart {
        public String loadingLanguage = "Loading language file: {0}...";
        public String loadLanguageSuccess = "Language file {0} loaded successfully.";
        public String loadLanguageFail = "Failed to load language file {0}: {1}";
    }

    public static class CommunicationText extends ConfigurationPart {
        // WebSocket Server
        public String websocketStarting = "Starting WebSocket server...";
        public String websocketStarted = "WebSocket server started on {0}:{1}";
        public String websocketFailed = "Failed to start WebSocket server!";
        public String websocketStopping = "Stopping WebSocket server...";
        public String websocketStopped = "WebSocket server stopped";
        public String wsServerStarted = "MCP Agent WebSocket server started on {0}:{1}";
        public String wsServerFailed = "Failed to start WebSocket server: {0}";
        public String wsBroadcastFailed = "Failed to broadcast to gateway {0}: {1}";
        public String wsConnectionError = "Error handling connection from gateway {0}: {1}";
        public String wsMessageHandlingError = "Failed to handle message from gateway {0}: {1}";
        public String wsGatewayReauthAttempt = "Gateway {0} attempted re-authentication";
        public String wsUnauthRequestAttempt = "Unauthenticated gateway {0} attempted request";
        public String wsUnknownMessageType = "Unknown message type from gateway {0}: {1}";

        // Session Manager
        public String sessionAdded = "Added session {0} for gateway {1}";
        public String sessionRemoved = "Removed session {0} for gateway {1}";
        public String sessionAuthTimeout = "Session {0} authentication timeout, closing connection";
        public String gatewayAuthenticated = "Gateway {0} authenticated successfully with {1} permissions";
        public String closedAllSessions = "Closed all sessions: {0}";
        public String removingStaleSession = "Removing stale session {0} (inactive for {1}s)";
        public String sessionCleanupError = "Error in session cleanup task: {0}";

        // Gateway Session
        public String closingSession = "Closing session {0} with code {1}: {2}";

        // Auth Handler
        public String authFailedInvalidToken = "Authentication failed for gateway {0}: Invalid token";
        public String gatewayPassedWhitelist = "Gateway {0} passed whitelist check";

        // Heartbeat Handler
        public String heartbeatHandlerStarted = "Heartbeat handler started (interval: {0}ms, timeout: {1}ms)";
        public String gatewayHeartbeatTimeout = "Gateway {0} heartbeat timeout (elapsed: {1}ms), closing connection";
        public String heartbeatCheckError = "Error in heartbeat check: {0}";
        public String heartbeatSendFailed = "Failed to send heartbeat to gateway {0}: {1}";
        public String heartbeatSendError = "Error sending heartbeat to gateway {0}: {1}";
        public String heartbeatAckReceived = "Received heartbeat ack from gateway {0}";

        // Message Codec
        public String codecEncodeFailed = "Failed to encode message: {0}";
        public String codecDecodeJsonFailed = "Failed to decode JSON: {0}";
        public String codecDecodeMessageFailed = "Failed to decode message: {0}";

        // Message Router
        public String routerRoutingMessage = "Routing message type: {0} (id: {1})";
        public String routerUnknownMessageType = "Unknown message type: {0}";
        public String routerMessageError = "Error routing message {0}: {1}";
        public String routerHandlingRequest = "Handling request: {0}";
        public String routerHandlingResponse = "Handling response: {0}";
        public String routerHandlingEvent = "Handling event: {0}";
        public String routerHandlingHeartbeat = "Handling heartbeat: {0}";
        public String routerHandlingHeartbeatAck = "Handling heartbeat_ack: {0}";
        public String routerHandlingAuth = "Handling auth: {0}";
        public String routerHandlingAuthResponse = "Handling auth_response: {0}";
    }

    public static ConfigurationText configurationText = new ConfigurationText();
    public static ModelContextProtocolAgent.MainClassText mainClassText = new ModelContextProtocolAgent.MainClassText();
    public static CommunicationText communicationText = new CommunicationText();

    public static void loadLanguageFiles(CommandSender sender, JavaPlugin plugin, String code) {
        try {
            // save default language files to the languages folder
            File languagesFolder = new File(ModelContextProtocolAgent.getInstance().getDataFolder(), "languages");
            for (LanguageCode languageCode : LanguageCode.values()) {
                updateLanguageFiles(plugin, languageCode.name(), false);
            }
            Notification.info(sender != null ? sender : ModelContextProtocolAgent.getInstance().getServer().getConsoleSender(), I18n.configurationText.loadingLanguage, code);
            ConfigurationManager.load(I18n.class, new File(languagesFolder, code + ".yml"));
            Notification.info(sender != null ? sender : ModelContextProtocolAgent.getInstance().getServer().getConsoleSender(), I18n.configurationText.loadLanguageSuccess, code);
        } catch (Exception e) {
            Notification.error(sender != null ? sender : ModelContextProtocolAgent.getInstance().getServer().getConsoleSender(), I18n.configurationText.loadLanguageFail, code, e.getMessage());
        }
    }

    public static void updateLanguageFiles(JavaPlugin plugin, String code, boolean overwrite) {
        File languagesFolder = new File(plugin.getDataFolder(), "languages");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdir();
        }
        if (!new File(languagesFolder, code + ".yml").exists()) try {
            ModelContextProtocolAgent.getInstance().saveResource("languages/" + code + ".yml", overwrite);
        } catch (Exception e) {
            XLogger.warn("Failed to save language file for {0}, This language may not in official repo : {1}.", code, e.getMessage());
        }
    }

}
