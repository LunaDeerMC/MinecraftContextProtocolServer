# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Minecraft Model Context Protocol (MCP) Agent** project - a plugin system that enables AI models to safely interact with Minecraft servers through standardized MCP capabilities. The project consists of two main components:

1. **Core Plugin** (`core/`) - Minecraft server plugin that provides MCP execution environment
2. **SDK** (`sdk/`) - Java library for other plugins to define MCP capabilities

## Build Commands

### Build the Plugin JAR
```bash
./gradlew "Clean&Build"
```
This runs clean + shadowJar, producing a JAR in `core/build/libs/` with version suffix `-lite` or `-full`.

### Build with Full Dependencies
```bash
./gradlew "Clean&Build" -PbuildFull=true
```
Produces a `-full` JAR with bundled dependencies instead of `compileOnly`.

### Build SDK Only
```bash
./gradlew :sdk:build
```

### Clean
```bash
./gradlew clean
```

## Architecture

### Multi-Module Gradle Structure
```
ModelContextProtocolAgent/
├── build.gradle.kts          # Root build with versioning logic
├── settings.gradle.kts       # Includes: sdk, core
├── version.properties        # Auto-generated version file (alpha/beta suffixes)
├── sdk/                      # Maven-published SDK library
│   ├── build.gradle.kts      # Maven publishing, signing, Sonatype upload
│   └── src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/
│       ├── annotations/      # @McpProvider, @McpAction, @McpContext, @McpEvent, @Param, @Result
│       ├── api/              # McpAgent, McpProviderRegistry, McpEventEmitter
│       ├── model/            # CapabilityManifest, RiskLevel, CapabilityType, ErrorCode
│       └── util/             # JsonUtil, SchemaGenerator
└── core/                     # Minecraft plugin
    ├── build.gradle.kts      # ShadowJar configuration, versioning
    └── src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/
        ├── ModelContextProtocolAgent.java    # Main plugin class (JavaPlugin)
        ├── Configuration.java                # Static config class
        ├── infrastructure/
        │   ├── configuration/                # YAML config management system
        │   │   ├── ConfigurationManager.java # Reflection-based YAML loader/saver
        │   │   ├── ConfigurationFile.java    # Base for config classes
        │   │   ├── ConfigurationPart.java    # Nested config sections
        │   │   ├── Comment/Comments.java     # Inline YAML comments
        │   │   └── ...
        │   ├── scheduler/                     # Paper/Spigot task abstraction (Folia-compatible)
        │   ├── XLogger.java                  # Debug/info/warn/error logging
        │   ├── Notification.java             # Player/Console messaging
        │   └── I18n.java                     # Internationalization system
        ├── communication/                     # WebSocket server & message handling
        │   ├── server/                        # AgentWebSocketServer (Java-WebSocket)
        │   ├── session/                       # SessionManager, GatewaySession
        │   ├── auth/                          # AuthHandler, AuthResult
        │   ├── heartbeat/                     # HeartbeatHandler
        │   ├── codec/                         # MessageCodec (JSON serialization)
        │   ├── router/                        # MessageRouter
        │   ├── message/                       # MCP message types
        │   └── handler/                       # Message handlers (Auth, Heartbeat, Request)
        ├── core/                              # Core MCP functionality
        │   ├── registry/                      # CapabilityRegistry, ProviderDescriptor
        │   ├── execution/                     # ExecutionEngine, ExecutionInterceptor
        │   ├── schema/                        # SchemaValidator
        │   ├── permission/                    # PermissionChecker
        │   └── audit/                         # AuditLogger, AuditEvent
        ├── http_sse/                          # HTTP SSE MCP server (MCP protocol)
        │   ├── HttpMcpServer.java             # Main MCP HTTP server
        │   ├── transport/                     # HttpSseTransport
        │   ├── handler/                       # InitializeHandler, ToolsHandler, etc.
        │   ├── lifecycle/                     # SessionManager, SessionInfo
        │   ├── message/                       # JsonRpcMessage, JsonRpcRequest, etc.
        │   └── tool/                          # McpTool, McpToolRequest, ToolDecorator
        ├── api/                               # Internal API implementations
        │   ├── McpAgentImpl.java              # McpAgent interface implementation
        │   ├── McpEventEmitterImpl.java       # Event emitter implementation
        │   └── command/                       # Admin commands (McpCommandManager)
        └── provider/builtin/                  # Built-in MCP providers
            ├── WorldProvider.java             # World-related capabilities
            ├── PlayerProvider.java            # Player-related capabilities
            ├── EntityProvider.java            # Entity-related capabilities
            ├── SystemProvider.java            # System-related capabilities
            ├── ChatProvider.java              # Chat-related capabilities
            └── BlockProvider.java             # Block-related capabilities
```

### Versioning System
- **Beta branch** (e.g., `main`): Version `1.0.0-beta`
- **Alpha branch** (e.g., `dev/*`): Version `1.0.0-alpha.N` (auto-incremented)
- Version is stored in `version.properties` and auto-updated by Gradle
- Root `build.gradle.kts` contains `getAndIncrementVersion()` function that:
  - Detects current git branch
  - Increments alpha version number on each build for dev branches
  - Resets to beta when switching to main branch
  - Uses `version.properties` to persist version state

### Configuration Management
The core uses a **reflection-based YAML configuration system**:

```java
// Define config with annotations
public class Configuration extends ConfigurationFile {
    @Comment("Language code")
    public static String language = "en_US";

    public static class WebsocketServer extends ConfigurationPart {
        @Comment("Host address")
        public String host = "127.0.0.1";
        @Comment("Port for websocket server")
        public int port = 8765;
        @Comment("Secret key for connection")
        public String authToken = "ChangeMe!";
    }

    public static class HttpSseMcpServer extends ConfigurationPart {
        @Comment("Enable or disable the internal MCP server on start")
        public boolean enableOnStart = false;
        @Comment("Host address for internal MCP server")
        public String host = "0.0.0.0";
        @Comment("Port for internal MCP server")
        public int port = 8766;
        @Comment("Bearer token for internal MCP server authentication")
        public String bearerToken = "ChangeMeToo!";
    }
}

// Load automatically on plugin enable
ConfigurationManager.load(Configuration.class, new File(getDataFolder(), "config.yml"));
```

**Key features:**
- Automatic camelCase → kebab-case conversion for YAML keys
- Inline comments via `@Comment` annotation
- Pre/post-process hooks for custom logic
- Nested `ConfigurationPart` support

## SDK Usage for Plugin Developers

### 1. Add SDK Dependency
```gradle
dependencies {
    compileOnly("cn.lunadeer.mc:ModelContextProtocolAgentSDK:1.0.0")
}
```

### 2. Define a Provider
```java
@McpProvider(
    id = "my-plugin",
    name = "My Plugin Capabilities",
    description = "Provides custom MCP capabilities"
)
public class MyProvider {

    @McpContext(
        id = "player.info",
        name = "Get Player Info",
        description = "Retrieve information about a player"
    )
    public PlayerInfo getPlayerInfo(@Param(name = "playerName") String name) {
        // ...
    }

    @McpAction(
        id = "player.teleport",
        name = "Teleport Player",
        description = "Teleport a player to a location",
        risk = RiskLevel.MEDIUM,
        permissions = {"myplugin.teleport"}
    )
    public void teleportPlayer(
        @Param(name = "playerName") String name,
        @Param(name = "location") LocationParam location
    ) {
        // ...
    }
}
```

### 3. Register the Provider
```java
@EventHandler
public void onEnable(PluginEnableEvent event) {
    McpAgent mcpAgent = Bukkit.getServicesManager().load(McpAgent.class);
    if (mcpAgent != null) {
        mcpAgent.getProviderRegistry().register(new MyProvider(), this);
    }
}
```

### 4. Emit Events
```java
McpEventEmitter emitter = mcpAgent.getEventEmitter();
emitter.emit("player.join", Map.of("player", playerName));
```

## Core Plugin Architecture

### Main Components

1. **ModelContextProtocolAgent** ([core/src/main/java/.../ModelContextProtocolAgent.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/ModelContextProtocolAgent.java))
   - Entry point for plugin lifecycle (extends `JavaPlugin`)
   - Initializes infrastructure (Logger, Scheduler, Notification, I18n)
   - Loads configuration on enable via `ConfigurationManager`
   - Starts WebSocket server on configurable host/port
   - Starts HTTP SSE MCP server (optional, for direct MCP client connections)
   - Registers built-in providers (World, Player, Entity, System, Chat, Block)
   - Registers MCP Agent as Bukkit service for plugin access
   - Provides static `getInstance()` for singleton access

2. **AgentWebSocketServer** ([core/src/main/java/.../communication/AgentWebSocketServer.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/AgentWebSocketServer.java))
   - WebSocket server using `org.java_websocket.server.WebSocketServer`
   - Uses `WebSocketServerImpl` inner class for Java-WebSocket integration
   - Handles authentication via `AuthHandler`
   - Manages sessions via `SessionManager` (5-minute timeout)
   - Routes messages via `MessageRouter`
   - Implements heartbeat mechanism via `HeartbeatHandler` (periodic pings)
   - Supports broadcasting to all authenticated sessions
   - Uses `WebSocketConnection` abstraction for connection handling

3. **Session Management** ([core/src/main/java/.../communication/session/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/session/))
   - `SessionManager` - Manages all Gateway connection sessions (ConcurrentHashMap)
   - `GatewaySession` - Individual session with authentication state, gateway ID
   - Session cleanup task removes stale connections (300s timeout)
   - Authentication timeout (30 seconds for initial auth)
   - `WebSocketConnection` interface for connection abstraction

4. **Message Protocol** ([core/src/main/java/.../communication/message/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/message/))
   - `AuthRequest` / `AuthResponse` - Authentication messages with gateway ID/token
   - `HeartbeatMessage` / `HeartbeatAck` - Connection health monitoring
   - `McpRequest` / `McpResponse` - Capability execution with parameters/results
   - `McpEvent` - Server-sent event broadcasting
   - `McpMessage` - Base message class with type field
   - Message types: "auth", "heartbeat", "heartbeat_ack", "request", "response", "event"

5. **CapabilityRegistry** ([core/src/main/java/.../core/registry/CapabilityRegistry.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/core/registry/CapabilityRegistry.java))
   - Implements `McpProviderRegistry` interface (from SDK)
   - Registers provider instances via reflection scanning
   - Scans for `@McpContext`, `@McpAction`, `@McpEvent` annotations on methods
   - Generates `CapabilityManifest` for each capability with full metadata
   - Uses `SchemaGenerator` to create JSON schemas from Java types
   - Maintains three indexes: capabilityId→descriptor, providerId→descriptor, instance→providerId
   - Supports provider unregistration and cleanup
   - Thread-safe using `ConcurrentHashMap`

6. **ExecutionEngine** ([core/src/main/java/.../core/execution/ExecutionEngine.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/core/execution/ExecutionEngine.java))
   - Processes capability invocation requests from `RequestMessageHandler`
   - Uses interceptor chain for pre/post-processing (permission checks, audit logging)
   - Handles parameter conversion and method invocation via reflection
   - Returns `McpResponse` with results or error codes
   - Supports caching for context capabilities (configurable TTL)
   - Validates parameters against JSON schema

7. **ConfigurationManager** ([core/src/main/java/.../infrastructure/configuration/ConfigurationManager.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/infrastructure/configuration/ConfigurationManager.java))
   - Reflection-based YAML loader/saver using Jackson
   - Supports nested configuration sections via `ConfigurationPart`
   - Preserves comments in YAML files via `@Comment` annotations
   - Automatic camelCase → kebab-case conversion for YAML keys
   - Pre/post-process hooks for validation/transformation
   - Loads from `plugins/ModelContextProtocolAgent/config.yml`
   - Language files stored in `plugins/ModelContextProtocolAgent/languages/`

8. **Message Handlers** ([core/src/main/java/.../communication/handler/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/handler/))
   - `AuthMessageHandler` - Handles authentication requests, validates tokens
   - `HeartbeatAckMessageHandler` - Responds to heartbeat acknowledgments
   - `RequestMessageHandler` - Routes capability execution requests to ExecutionEngine
   - Handlers registered via `MessageRouter` in `AgentWebSocketServer`

9. **HTTP SSE MCP Server** ([core/src/main/java/.../http_sse/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/http_sse/))
   - `HttpMcpServer` - Main MCP HTTP server for direct MCP client connections
   - `HttpSseTransport` - HTTP transport implementing MCP protocol
   - `ToolsHandler` - Handles tools/list and tools/call requests
   - `InitializeHandler` - Handles MCP initialization
   - `InitializedHandler` - Handles MCP initialized state
   - `ParameterConverter` - Converts JSON parameters to Java types
   - `McpTool` / `McpToolRequest` / `McpToolResult` - Tool abstraction layer
   - Supports standard MCP protocol for direct client connections (e.g., Claude Code)

10. **Built-in Providers** ([core/src/main/java/.../provider/builtin/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/provider/builtin/))
   - `WorldProvider` - World management, time, weather, chunk operations
   - `PlayerProvider` - Player info, inventory, teleportation, permissions
   - `EntityProvider` - Entity spawning, removal, modification
   - `SystemProvider` - Server info, performance metrics, plugin management
   - `ChatProvider` - Chat messaging, broadcast, formatting
   - `BlockProvider` - Block info, setting, batch operations
   - All providers use `@McpProvider` and `@McpAction`/`@McpContext` annotations

### International & Textingualization
- `I18n` class manages language files and translations
- Defines text keys in `XXXXXText` classes in coresponding classes
- Instance `XXXXText` object in `I18n` for access
- All log/notification messages should defined as above for localization support

### WebSocket Protocol Flow

1. **Connection**: Client connects to WebSocket endpoint (default: `ws://127.0.0.1:8765`)
2. **Authentication**: Client sends `AuthRequest` with gateway ID and token
3. **Capability Discovery**: Server responds with `AuthResponse` containing `CapabilityManifest` list
4. **Heartbeat**: Server sends `HeartbeatMessage` periodically (configurable), client responds with `HeartbeatAck`
5. **Request/Response**: Client sends `McpRequest`, server executes capability and returns `McpResponse`
6. **Events**: Server broadcasts `McpEvent` to all authenticated sessions

### HTTP SSE MCP Protocol Flow

1. **Connection**: Client connects to HTTP endpoint (default: `http://0.0.0.0:8766/mcp`)
2. **Authentication**: Client provides Bearer token in `Authorization` header
3. **Initialization**: Client sends `initialize` request, server responds with capabilities
4. **Tools List**: Client sends `tools/list` request to discover available capabilities
5. **Tools Call**: Client sends `tools/call` request to execute a capability
6. **Events**: Server can send SSE events to subscribed clients

### Message Types

- **type: "auth"** - Authentication request/response
  - Request: `gatewayId`, `token`
  - Response: `success`, `capabilities` (list of manifests), `agentId`, `version`
- **type: "heartbeat"** - Heartbeat ping (server → client)
- **type: "heartbeat_ack"** - Heartbeat pong (client → server)
- **type: "request"** - Capability execution request
  - Fields: `capabilityId`, `parameters`, `requestId`, `sessionId`
- **type: "response"** - Capability execution response
  - Fields: `requestId`, `success`, `result`, `error` (code, message)
- **type: "event"** - Server-sent event
  - Fields: `eventId`, `data`, `timestamp`

### Authentication & Security

- **Token-based authentication**: Gateway must provide valid token in `AuthRequest`
- **Session timeout**: 30 seconds for initial authentication
- **Connection timeout**: 5 minutes for idle connections (heartbeat monitoring)
- **Permission checks**: `@McpAction` annotations specify required Bukkit permissions
- **Risk levels**: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` for capability classification
- **Audit logging**: All capability executions logged with timestamp, user, parameters

## Testing

### Running Tests
```bash
./gradlew test
```

### Run a Single Test
```bash
# Run tests in SDK module matching a pattern
./gradlew :sdk:test --tests "*SchemaGenerator*"

# Run tests in core module matching a pattern
./gradlew :core:test --tests "*ConfigurationManager*"
```

### Integration Testing
Since this is a Minecraft plugin, integration tests require:
1. Build the plugin JAR: `./gradlew "Clean&Build"`
2. Install on a Paper/Spigot server
3. Test with actual Minecraft clients
4. Use RunPaper for local testing: `./gradlew runPaper`

### Development Workflow
1. Make changes to SDK or core
2. Run tests: `./gradlew test`
3. Build plugin: `./gradlew "Clean&Build"`
4. Deploy to local test server or use RunPaper
5. Test WebSocket connection with a client

## Important Notes

### Java Version
- Requires Java 17+ (configured in toolchain)
- Minecraft 1.20.1+ compatible
- UTF-8 encoding enforced for all compilation tasks

### Dependencies
- **Core**:
  - Paper API 1.20.1-R0.1-SNAPSHOT
  - Adventure platform for Bukkit 4.3.3
  - Java-WebSocket 1.5.4
  - Jackson (via ConfigurationManager)
  - Gson (for HTTP SSE MCP server)
- **SDK**:
  - Paper API 1.20.1-R0.1-SNAPSHOT (compileOnly)
  - JUnit Jupiter 5.10.0 (test)
- **Build**:
  - Shadow plugin 8.1.1 for fat JAR
  - RunPaper 2.3.1 for dev server
  - Maven Publish & Signing for SDK distribution
  - Sonatype Uploader plugin for Maven Central

### Folia Support
The plugin is marked as Folia-compatible in `plugin.yml`. The `Scheduler` abstraction handles async operations properly and works with both Paper and Folia servers.

### Configuration File Location
- Main config: `plugins/ModelContextProtocolAgent/config.yml`
- Language files: `plugins/ModelContextProtocolAgent/languages/`
- Default language: `en_US` (configurable via `language` setting)

### Built-in Capabilities
The plugin includes 6 built-in providers with ~30+ capabilities:
- **WorldProvider**: List worlds, get time/weather, set time/weather
- **PlayerProvider**: List players, get player info, teleport, kick, ban
- **EntityProvider**: Spawn entities, remove entities, get entity info
- **SystemProvider**: Server info, performance metrics, plugin list
- **ChatProvider**: Send messages, broadcast, clear chat
- **BlockProvider**: Get block info, set blocks, batch operations

### External Dependencies
When `buildFull=false` (default), these dependencies must be provided by the server:
- Paper API (provided by Paper server)
- Adventure platform (provided by Paper server)
- Java-WebSocket (must be installed separately or use `-full` build)

### Build Variants
- **Lite build** (`-lite`): External dependencies, smaller JAR (~50KB)
- **Full build** (`-full`): Bundled dependencies, larger JAR (~2MB)

## Key Files Reference

### Core Plugin
- **Main plugin class**: [core/src/main/java/.../ModelContextProtocolAgent.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/ModelContextProtocolAgent.java)
- **Configuration definition**: [core/src/main/java/.../Configuration.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/Configuration.java)
- **Config manager**: [core/src/main/java/.../infrastructure/configuration/ConfigurationManager.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/infrastructure/configuration/ConfigurationManager.java)
- **WebSocket server**: [core/src/main/java/.../communication/AgentWebSocketServer.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/AgentWebSocketServer.java)
- **Session manager**: [core/src/main/java/.../communication/session/SessionManager.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/session/SessionManager.java)
- **Capability registry**: [core/src/main/java/.../core/registry/CapabilityRegistry.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/core/registry/CapabilityRegistry.java)
- **Execution engine**: [core/src/main/java/.../core/execution/ExecutionEngine.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/core/execution/ExecutionEngine.java)
- **Message handlers**: [core/src/main/java/.../communication/handler/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/communication/handler/)
- **Built-in providers**: [core/src/main/java/.../provider/builtin/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/provider/builtin/)
- **HTTP SSE MCP Server**: [core/src/main/java/.../http_sse/HttpMcpServer.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/http_sse/HttpMcpServer.java)
- **HTTP SSE handlers**: [core/src/main/java/.../http_sse/handler/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/http_sse/handler/)
- **HTTP SSE transport**: [core/src/main/java/.../http_sse/transport/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/http_sse/transport/)
- **HTTP SSE tools**: [core/src/main/java/.../http_sse/tool/](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/http_sse/tool/)

### SDK
- **SDK main interface**: [sdk/src/main/java/.../api/McpAgent.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/api/McpAgent.java)
- **Provider registry**: [sdk/src/main/java/.../api/McpProviderRegistry.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/api/McpProviderRegistry.java)
- **Event emitter**: [sdk/src/main/java/.../api/McpEventEmitter.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/api/McpEventEmitter.java)
- **Annotations**: [sdk/src/main/java/.../annotations/](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/annotations/)
  - `@McpProvider` - Class-level annotation for provider definition
  - `@McpAction` - Method-level annotation for action capabilities
  - `@McpContext` - Method-level annotation for context capabilities
  - `@McpEvent` - Method-level annotation for event definitions
  - `@Param` - Parameter-level annotation for parameter metadata
  - `@Result` - Record component annotation for return value metadata
- **Capability manifest**: [sdk/src/main/java/.../model/CapabilityManifest.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/model/CapabilityManifest.java)
- **Schema generator**: [sdk/src/main/java/.../util/SchemaGenerator.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/util/SchemaGenerator.java)
- **Model classes**: [sdk/src/main/java/.../model/](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/model/)
  - `RiskLevel` - Risk classification for capabilities
  - `CapabilityType` - Type enum (CONTEXT, ACTION, EVENT)
  - `ErrorCode` - Standardized error codes
- **Exceptions**: [sdk/src/main/java/.../exception/](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/exception/)
  - `McpException` - Base exception
  - `McpBusinessException` - Business logic errors
  - `McpSecurityException` - Security-related errors
  - `McpValidationException` - Parameter validation errors
- **DTOs**: [sdk/src/main/java/.../model/dto/](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/model/dto/)
  - `LocationParam` - Location data transfer object
  - `PaginationParam` - Pagination parameters
  - `PlayerInfo` - Player information
  - `BlockInfo` - Block information
  - `TpsResult` - TPS query result
  - `TeleportResult` - Teleport operation result
  - `KickResult` - Kick operation result
  - `WeatherResult` - Weather query result
  - `SetWeatherResult` - Weather set result
  - `WorldTimeResult` - World time query result
  - `SetTimeResult` - World time set result
  - `WeatherType` - Weather type enum
  - `DirectionType` - Block direction enum
  - `BlockLocationParam` - Block location data transfer object
  - `BlockSetting` - Block setting for batch operations
  - `BlockListResult` - Block list with pagination
- **Utilities**: [sdk/src/main/java/.../util/](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/util/)
  - `JsonUtil` - JSON serialization and deserialization utilities

### Build Configuration
- **Root build**: [build.gradle.kts](build.gradle.kts) - Versioning, multi-module setup
- **SDK build**: [sdk/build.gradle.kts](sdk/build.gradle.kts) - Maven publishing, signing
- **Core build**: [core/build.gradle.kts](core/build.gradle.kts) - ShadowJar, dependencies
- **Settings**: [settings.gradle.kts](settings.gradle.kts) - Module inclusion
- **Version file**: [version.properties](version.properties) - Auto-generated version state
