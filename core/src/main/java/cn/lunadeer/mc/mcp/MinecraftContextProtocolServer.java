package cn.lunadeer.mc.mcp;

import cn.lunadeer.mc.mcp.api.McpEventEmitterImpl;
import cn.lunadeer.mc.mcp.api.McpServerImpl;
import cn.lunadeer.mc.mcp.api.command.McpCommandManager;
import cn.lunadeer.mc.mcp.communication.WebSocketServer;
import cn.lunadeer.mc.mcp.core.audit.AuditLogger;
import cn.lunadeer.mc.mcp.core.execution.ExecutionEngine;
import cn.lunadeer.mc.mcp.core.execution.ExecutionInterceptor;
import cn.lunadeer.mc.mcp.core.permission.PermissionChecker;
import cn.lunadeer.mc.mcp.core.registry.CapabilityRegistry;
import cn.lunadeer.mc.mcp.http_sse.HttpServer;
import cn.lunadeer.mc.mcp.infrastructure.I18n;
import cn.lunadeer.mc.mcp.infrastructure.Notification;
import cn.lunadeer.mc.mcp.infrastructure.XLogger;
import cn.lunadeer.mc.mcp.infrastructure.configuration.ConfigurationManager;
import cn.lunadeer.mc.mcp.infrastructure.configuration.ConfigurationPart;
import cn.lunadeer.mc.mcp.infrastructure.scheduler.Scheduler;
import cn.lunadeer.mc.mcp.provider.builtin.*;
import cn.lunadeer.mc.mcp.sdk.api.McpServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class MinecraftContextProtocolServer extends JavaPlugin {

    private WebSocketServer webSocketServer;
    private HttpServer httpServer;
    private McpServerImpl mcpAgent;
    private CapabilityRegistry capabilityRegistry;
    private McpEventEmitterImpl eventEmitter;
    private McpCommandManager commandManager;
    private ExecutionEngine executionEngine;

    public static class MainClassText extends ConfigurationPart {
        public String loadingConfig = "Loading configuration...";
        public String configLoadFailed = "Configuration load failed!";
        public String configLoaded = "Configuration loaded!";
        public String websocketStarting = "Starting WebSocket server...";
        public String websocketStarted = "WebSocket server started on {0}:{1}";
        public String websocketFailed = "Failed to start WebSocket server!";
        public String websocketStopping = "Stopping WebSocket server...";
        public String websocketStopped = "WebSocket server stopped";
        public String httpServerStarting = "Starting MCP HTTP server...";
        public String httpServerStarted = "MCP HTTP server started on {0}:{1}";
        public String httpServerFailed = "Failed to start MCP HTTP server!";
        public String httpServerStopping = "Stopping MCP HTTP server...";
        public String httpServerStopped = "MCP HTTP server stopped";
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        new Notification(this);
        new XLogger(this);
        XLogger.setDebug(true);
        new Scheduler(this);

        // https://patorjk.com/software/taag/#p=display&f=Small&t=MCP-Server&x=none&v=4&h=4&w=80&we=false
        XLogger.info("  __  __  ___ ___     ___                      ");
        XLogger.info(" |  \\/  |/ __| _ \\___/ __| ___ _ ___ _____ _ _ ");
        XLogger.info(" | |\\/| | (__|  _/___\\__ \\/ -_) '_\\ V / -_) '_|");
        XLogger.info(" |_|  |_|\\___|_|     |___/\\___|_|  \\_/\\___|_|  ");

        loadConfiguration();
        initializeProviderLayer();
        registerBuiltInProviders();
        if (Configuration.websocketServer.enableOnStart) startWebSocketServer();
        if (Configuration.httpSseMcpServer.enableOnStart) startHttpMcpServer();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        stopWebSocketServer();
        stopHttpMcpServer();
    }

    private static MinecraftContextProtocolServer instance;

    public static MinecraftContextProtocolServer getInstance() {
        return instance;
    }

    /**
     * Load the configuration file and language files.
     */
    public void loadConfiguration() {
        try {
            XLogger.info(I18n.mainClassText.loadingConfig);
            ConfigurationManager.load(Configuration.class, new File(getDataFolder(), "config.yml"));
            XLogger.setDebug(Configuration.debug);
            XLogger.info(I18n.mainClassText.configLoaded);
            I18n.loadLanguageFiles(null, this, Configuration.language);
        } catch (Exception e) {
            XLogger.warn(I18n.mainClassText.configLoadFailed);
            XLogger.error(e);
        }
    }

    /**
     * Starts the WebSocket server.
     */
    public void startWebSocketServer() {
        try {
            XLogger.info(I18n.mainClassText.websocketStarting);
            webSocketServer = new WebSocketServer(
                    Configuration.websocketServer.host,
                    Configuration.websocketServer.port,
                    executionEngine
            );
            webSocketServer.start();
            XLogger.info(I18n.mainClassText.websocketStarted,
                    Configuration.websocketServer.host,
                    Configuration.websocketServer.port);
        } catch (Exception e) {
            XLogger.warn(I18n.mainClassText.websocketFailed);
            XLogger.error(e);
        }
    }

    /**
     * Stops the WebSocket server.
     */
    public void stopWebSocketServer() {
        if (webSocketServer != null) {
            XLogger.info(I18n.mainClassText.websocketStopping);
            webSocketServer.stop();
            webSocketServer = null;
            XLogger.info(I18n.mainClassText.websocketStopped);
        }
    }

    /**
     * Gets the WebSocket server instance.
     *
     * @return the WebSocket server
     */
    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }

    /**
     * Starts the MCP HTTP server.
     */
    public void startHttpMcpServer() {
        try {
            XLogger.info(I18n.mainClassText.httpServerStarting);
            httpServer = new HttpServer(
                    Configuration.httpSseMcpServer.host,
                    Configuration.httpSseMcpServer.port,
                    Configuration.httpSseMcpServer.bearerToken,
                    capabilityRegistry,
                    Configuration.serverInfo.serverId,
                    Configuration.serverInfo.serverName,
                    Configuration.serverInfo.serverVersion
            );
            httpServer.start();
            XLogger.info(I18n.mainClassText.httpServerStarted,
                    Configuration.httpSseMcpServer.host,
                    Configuration.httpSseMcpServer.port);
        } catch (Exception e) {
            XLogger.warn(I18n.mainClassText.httpServerFailed);
            XLogger.error(e);
        }
    }

    /**
     * Stops the MCP HTTP server.
     */
    public void stopHttpMcpServer() {
        if (httpServer != null) {
            XLogger.info(I18n.mainClassText.httpServerStopping);
            httpServer.stop();
            httpServer = null;
            XLogger.info(I18n.mainClassText.httpServerStopped);
        }
    }

    /**
     * Gets the MCP HTTP server instance.
     *
     * @return the MCP HTTP server
     */
    public HttpServer getHttpMcpServer() {
        return httpServer;
    }

    /**
     * Initializes the provider layer.
     */
    private void initializeProviderLayer() {
        capabilityRegistry = new CapabilityRegistry();
        eventEmitter = new McpEventEmitterImpl();
        mcpAgent = new McpServerImpl(capabilityRegistry, eventEmitter, getDescription().getVersion(), Configuration.serverInfo.serverId);

        // Create execution interceptors
        List<ExecutionInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new PermissionChecker());
        interceptors.add(new AuditLogger());

        // Create execution engine
        executionEngine = new ExecutionEngine(capabilityRegistry, interceptors);

        // Register the McpAgent service with Bukkit's service manager
        getServer().getServicesManager().register(McpServer.class, mcpAgent, this, org.bukkit.plugin.ServicePriority.Normal);

        XLogger.info("Provider layer initialized");
    }

    /**
     * Registers built-in providers.
     */
    private void registerBuiltInProviders() {
        try {
            capabilityRegistry.register(new WorldProvider(), this);
            capabilityRegistry.register(new PlayerProvider(), this);
            capabilityRegistry.register(new EntityProvider(), this);
            capabilityRegistry.register(new SystemProvider(), this);
            capabilityRegistry.register(new ChatProvider(), this);
            capabilityRegistry.register(new BlockProvider(), this);

            int totalCapabilities = capabilityRegistry.getCapabilities().size();
            XLogger.info("Registered " + totalCapabilities + " built-in capabilities");
        } catch (Exception e) {
            XLogger.warn("Failed to register built-in providers");
            XLogger.error(e);
        }
    }

    /**
     * Gets the MCP Agent implementation.
     *
     * @return the MCP Agent
     */
    public McpServerImpl getMcpAgent() {
        return mcpAgent;
    }

    /**
     * Gets the capability registry.
     *
     * @return the capability registry
     */
    public CapabilityRegistry getCapabilityRegistry() {
        return capabilityRegistry;
    }

    /**
     * Gets the event emitter.
     *
     * @return the event emitter
     */
    public McpEventEmitterImpl getEventEmitter() {
        return eventEmitter;
    }

    /**
     * Registers admin commands.
     */
    private void registerCommands() {
        commandManager = new McpCommandManager(this);
        XLogger.info("Admin commands registered");
    }
}
