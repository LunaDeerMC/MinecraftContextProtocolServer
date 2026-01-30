package cn.lunadeer.mc.mcp.infrastructure;

import cn.lunadeer.mc.mcp.MinecraftContextProtocolServer;
import cn.lunadeer.mc.mcp.server.websocket_gateway.WebSocketServer;
import cn.lunadeer.mc.mcp.server.websocket_gateway.MessageRouter;
import cn.lunadeer.mc.mcp.server.websocket_gateway.auth.AuthHandler;
import cn.lunadeer.mc.mcp.server.websocket_gateway.codec.MessageCodec;
import cn.lunadeer.mc.mcp.server.websocket_gateway.heartbeat.HeartbeatHandler;
import cn.lunadeer.mc.mcp.server.websocket_gateway.session.GatewaySession;
import cn.lunadeer.mc.mcp.server.websocket_gateway.session.SessionManager;
import cn.lunadeer.mc.mcp.core.audit.AuditLogger;
import cn.lunadeer.mc.mcp.core.execution.ExecutionChain;
import cn.lunadeer.mc.mcp.core.execution.ExecutionEngine;
import cn.lunadeer.mc.mcp.core.permission.PermissionChecker;
import cn.lunadeer.mc.mcp.core.registry.CapabilityRegistry;
import cn.lunadeer.mc.mcp.core.schema.SchemaValidator;
import cn.lunadeer.mc.mcp.infrastructure.configuration.*;
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

    public static ConfigurationText configurationText = new ConfigurationText();
    public static MinecraftContextProtocolServer.MainClassText mainClassText = new MinecraftContextProtocolServer.MainClassText();

    // communication
    public static MessageCodec.MessageCodecText messageCodecText = new MessageCodec.MessageCodecText();
    public static HeartbeatHandler.HeartbeatHandlerText heartbeatHandlerText = new HeartbeatHandler.HeartbeatHandlerText();
    public static AuthHandler.AuthHandlerText authHandlerText = new AuthHandler.AuthHandlerText();
    public static SessionManager.SessionManagerText sessionManagerText = new SessionManager.SessionManagerText();
    public static GatewaySession.GatewaySessionText gatewaySessionText = new GatewaySession.GatewaySessionText();
    public static WebSocketServer.WebSocketServerText webSocketServerText = new WebSocketServer.WebSocketServerText();
    public static MessageRouter.MessageRouterText messageRouterText = new MessageRouter.MessageRouterText();

    // core
    public static ExecutionChain.ExecutionChainText executionChainText = new ExecutionChain.ExecutionChainText();
    public static SchemaValidator.SchemaValidatorText schemaValidatorText = new SchemaValidator.SchemaValidatorText();
    public static AuditLogger.AuditLoggerText auditLoggerText = new AuditLogger.AuditLoggerText();
    public static PermissionChecker.PermissionCheckerText permissionCheckerText = new PermissionChecker.PermissionCheckerText();
    public static ExecutionEngine.ExecutionEngineText executionEngineText = new ExecutionEngine.ExecutionEngineText();
    public static CapabilityRegistry.CapabilityRegistryText capabilityRegistryText = new CapabilityRegistry.CapabilityRegistryText();

    public static void loadLanguageFiles(CommandSender sender, JavaPlugin plugin, String code) {
        try {
            // save default language files to the languages folder
            File languagesFolder = new File(MinecraftContextProtocolServer.getInstance().getDataFolder(), "languages");
            for (LanguageCode languageCode : LanguageCode.values()) {
                updateLanguageFiles(plugin, languageCode.name(), false);
            }
            Notification.info(sender != null ? sender : MinecraftContextProtocolServer.getInstance().getServer().getConsoleSender(), I18n.configurationText.loadingLanguage, code);
            ConfigurationManager.load(I18n.class, new File(languagesFolder, code + ".yml"));
            Notification.info(sender != null ? sender : MinecraftContextProtocolServer.getInstance().getServer().getConsoleSender(), I18n.configurationText.loadLanguageSuccess, code);
        } catch (Exception e) {
            Notification.error(sender != null ? sender : MinecraftContextProtocolServer.getInstance().getServer().getConsoleSender(), I18n.configurationText.loadLanguageFail, code, e.getMessage());
        }
    }

    public static void updateLanguageFiles(JavaPlugin plugin, String code, boolean overwrite) {
        File languagesFolder = new File(plugin.getDataFolder(), "languages");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdir();
        }
        if (!new File(languagesFolder, code + ".yml").exists()) try {
            MinecraftContextProtocolServer.getInstance().saveResource("languages/" + code + ".yml", overwrite);
        } catch (Exception e) {
            XLogger.warn("Failed to save language file for {0}, This language may not in official repo : {1}.", code, e.getMessage());
        }
    }

}
