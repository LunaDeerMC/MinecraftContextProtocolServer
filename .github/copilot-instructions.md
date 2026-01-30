# MCP Agent Development Instructions

## Project Overview

This is a **Minecraft Model Context Protocol (MCP) Agent** - a plugin system that enables AI models to safely interact with Minecraft servers through standardized MCP capabilities. The project consists of two main components:

1. **Core Plugin** (`core/`) - Minecraft server plugin providing MCP execution environment
2. **SDK** (`sdk/`) - Java library for other plugins to define MCP capabilities

## Architecture & Data Flow

### Multi-Module Structure
```
ModelContextProtocolAgent/
├── build.gradle.kts          # Root build with versioning logic
├── settings.gradle.kts       # Includes: sdk, core
├── version.properties        # Auto-generated version file
├── sdk/                      # Maven-published SDK library
│   ├── annotations/          # @McpProvider, @McpAction, @McpContext, @McpEvent, @Param, @Result
│   ├── api/                  # McpAgent, McpProviderRegistry, McpEventEmitter
│   ├── model/                # CapabilityManifest, RiskLevel, CapabilityType, ErrorCode
│   └── util/                 # JsonUtil, SchemaGenerator
└── core/                     # Minecraft plugin
    ├── infrastructure/       # Configuration, Logger, Scheduler, I18n
    ├── communication/        # WebSocket server & message handling
    ├── core/                 # CapabilityRegistry, ExecutionEngine, SchemaValidator
    ├── http_sse/             # HTTP SSE MCP server (MCP protocol)
    ├── api/                  # Internal API implementations
    └── provider/builtin/     # Built-in MCP providers (World, Player, Entity, etc.)
```

### Critical Data Flow

1. **Capability Registration Flow**:
   - Plugin startup → `CapabilityRegistry` scans for `@McpProvider` classes
   - Reflection scans methods with `@McpAction`, `@McpContext`, `@McpEvent`
   - Generates `CapabilityManifest` with JSON schemas via `SchemaGenerator`
   - Stores in `ConcurrentHashMap` for thread-safe access

2. **WebSocket Request Flow**:
   - Client connects → `AuthHandler` validates token
   - `SessionManager` creates `GatewaySession` (5min timeout)
   - `MessageRouter` routes to `RequestMessageHandler`
   - `ExecutionEngine` processes with interceptor chain (permissions → audit → execution)
   - Returns `McpResponse` with result or error

3. **HTTP SSE MCP Flow**:
   - Client connects → Bearer token authentication
   - `InitializeHandler` processes `initialize` request
   - `ToolsHandler` provides `tools/list` and `tools/call`
   - `HttpSseTransport` handles MCP protocol directly

## Key Developer Workflows

### Build Commands
```bash
# Build plugin JAR (lite version - external dependencies)
./gradlew "Clean&Build"

# Build plugin JAR (full version - bundled dependencies)
./gradlew "Clean&Build" -PbuildFull=true

# Build SDK only
./gradlew :sdk:build

# Run tests
./gradlew test
```

### Versioning System
- **Beta branch** (`main`): Version `1.0.0-beta`
- **Alpha branch** (`dev/*`): Version `1.0.0-alpha.N` (auto-incremented)
- Version stored in `version.properties` and auto-updated by Gradle
- Root `build.gradle.kts` contains `getAndIncrementVersion()` function that detects git branch and increments version

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
    }
}

// Load automatically on plugin enable
ConfigurationManager.load(Configuration.class, new File(getDataFolder(), "config.yml"));
```

**Key features**:
- Automatic camelCase → kebab-case conversion for YAML keys
- Inline comments via `@Comment` annotation
- Nested `ConfigurationPart` support
- Pre/post-process hooks for custom logic

### Testing Workflow
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew :core:test --tests "*ConfigurationManager*"
./gradlew :sdk:test --tests "*SchemaGenerator*"
```

**Integration testing** requires:
1. Build plugin: `./gradlew "Clean&Build"`
2. Install on Paper/Spigot server
3. Test with actual Minecraft clients or use RunPaper: `./gradlew runPaper`

## Project-Specific Conventions

### SDK Development Patterns

#### 1. Provider Definition
```java
@McpProvider(
    id = "my-plugin",                    // lowercase, hyphens, unique
    name = "My Plugin Capabilities",
    description = "Provides custom MCP capabilities"
)
public class MyProvider {
    // Implementation
}
```

#### 2. Action vs Context Capabilities
- **Context** (`@McpContext`): Read-only, no side effects, can be cached
  - Use for queries: `player.info.get`, `world.time.get`
  - Lower risk, faster execution
- **Action** (`@McpAction`): Write operations, modifies state
  - Use for modifications: `player.teleport`, `world.time.set`
  - Requires risk assessment, permissions, audit logging

#### 3. Risk Level Classification
```java
RiskLevel.LOW    // Minimal impact (get info, list players)
RiskLevel.MEDIUM // Moderate impact (teleport, set time)
RiskLevel.HIGH   // Significant impact (ban, remove entities)
RiskLevel.CRITICAL // Server-wide impact (plugin reload, backup)
```

#### 4. Parameter & Return Types
- Use `@Param` for method parameters
- Use `Record` classes for complex return types with `@Result` annotations
- Leverage `LocationParam`, `PaginationParam` DTOs from SDK
- Schema auto-generated from Java types

#### 5. Exception Handling
```java
// Business logic errors
throw new McpBusinessException(
    ErrorCode.OPERATION_FAILED.getErrorCode(),
    "World not found: " + worldName
);

// Security violations
throw new McpSecurityException(
    ErrorCode.PERMISSION_DENIED.getErrorCode(),
    "Insufficient permissions"
);
```

### Core Plugin Patterns

#### 1. Configuration Management
- All config in `Configuration.java` extends `ConfigurationFile`
- Use `@Comment` and `@Comments` for inline YAML documentation
- Access via static fields: `Configuration.websocketServer.host`
- Load in `ModelContextProtocolAgent.onEnable()`

#### 2. Internationalization (I18n)
- All user-facing text in `I18n.java`
- Define text in nested `ConfigurationText` classes
- Use `§` color codes or `&` alternative
- Access via static fields: `I18n.configurationText.loadingLanguage`

#### 3. Session Management
- `SessionManager` uses `ConcurrentHashMap` for thread safety
- Sessions timeout after 5 minutes (configurable)
- Authentication timeout: 30 seconds
- Clean up stale sessions via scheduled task

#### 4. Message Protocol
All messages extend `McpMessage` with type field:
- `"auth"`: Authentication request/response
- `"heartbeat"` / `"heartbeat_ack"`: Connection health
- `"request"`: Capability execution
- `"response"`: Execution result
- `"event"`: Server-sent events

#### 5. Execution Engine Interceptors
Execution chain: Permission → Audit → Snapshot → Execution
- `PermissionChecker`: Validates Bukkit permissions
- `AuditLogger`: Logs all capability executions
- `ExecutionEngine`: Invokes method via reflection

### Built-in Providers
The plugin includes 6 built-in providers:

1. **WorldProvider** (`world.*`)
   - `world.time.get`, `world.time.set`
   - `world.weather.get`, `world.weather.set`
   - `world.list`

2. **PlayerProvider** (`player.*`)
   - `player.info.get`, `player.list`
   - `player.teleport`, `player.kick`, `player.ban`

3. **EntityProvider** (`entity.*`)
   - `entity.list`, `entity.spawn`, `entity.remove`

4. **SystemProvider** (`system.*`)
   - `system.info`, `system.backup`, `system.reload`

5. **ChatProvider** (`chat.*`)
   - `chat.send`, `chat.broadcast`, `chat.clear`

6. **BlockProvider** (`block.*`)
   - `block.info.get`, `block.set`, `block.batch.set`

### External Dependencies & Build Variants

**Lite build** (default): External dependencies, smaller JAR (~50KB)
- Paper API (provided by server)
- Adventure platform (provided by Paper)
- Java-WebSocket (must be installed separately)

**Full build** (`-PbuildFull=true`): Bundled dependencies, larger JAR (~2MB)
- All dependencies bundled in JAR

## Integration Points

### 1. WebSocket Gateway Connection
```
ws://127.0.0.1:8765
Auth: AuthRequest with gatewayId + token
Protocol: JSON messages over WebSocket
```

### 2. HTTP SSE MCP Server
```
http://0.0.0.0:8766/mcp
Auth: Bearer token in Authorization header
Protocol: Standard MCP protocol (JSON-RPC over HTTP/SSE)
```

### 3. Plugin Registration
```java
@EventHandler
public void onEnable(PluginEnableEvent event) {
    McpAgent mcpAgent = Bukkit.getServicesManager().load(McpAgent.class);
    if (mcpAgent != null) {
        mcpAgent.getProviderRegistry().register(new MyProvider(), this);
    }
}
```

### 4. Event Emission
```java
McpEventEmitter emitter = mcpAgent.getEventEmitter();
emitter.emit("player.join", Map.of("player", playerName));
```

## Important Notes

### Java Version & Compatibility
- **Java 17+** required (configured in toolchain)
- **Minecraft 1.20.1+** compatible
- **Folia** compatible (via `folia-supported: true` in plugin.yml)
- UTF-8 encoding enforced for all compilation

### File Locations
- Config: `plugins/ModelContextProtocolAgent/config.yml`
- Languages: `plugins/ModelContextProtocolAgent/languages/`
- Logs: Server console via `XLogger`

### Security Considerations
- **Token-based auth**: Gateway must provide valid token
- **Permission checks**: `@McpAction.permissions()` validated via Bukkit
- **Risk levels**: Higher risk requires more safety measures
- **Audit logging**: All executions logged with timestamp, user, parameters
- **Session timeouts**: 30s auth timeout, 5min idle timeout

### Folia Support
The plugin is Folia-compatible. The `Scheduler` abstraction handles async operations properly for both Paper and Folia servers.

## Key Files Reference

### Core Plugin
- **Main class**: `core/src/main/java/.../ModelContextProtocolAgent.java`
- **Config definition**: `core/src/main/java/.../Configuration.java`
- **Config manager**: `core/src/main/java/.../infrastructure/configuration/ConfigurationManager.java`
- **WebSocket server**: `core/src/main/java/.../communication/AgentWebSocketServer.java`
- **Session manager**: `core/src/main/java/.../communication/session/SessionManager.java`
- **Capability registry**: `core/src/main/java/.../core/registry/CapabilityRegistry.java`
- **Execution engine**: `core/src/main/java/.../core/execution/ExecutionEngine.java`
- **HTTP SSE server**: `core/src/main/java/.../http_sse/HttpMcpServer.java`
- **Built-in providers**: `core/src/main/java/.../provider/builtin/`

### SDK
- **Main interface**: `sdk/src/main/java/.../api/McpAgent.java`
- **Provider registry**: `sdk/src/main/java/.../api/McpProviderRegistry.java`
- **Event emitter**: `sdk/src/main/java/.../api/McpEventEmitter.java`
- **Annotations**: `sdk/src/main/java/.../annotations/`
- **Model classes**: `sdk/src/main/java/.../model/`
- **Schema generator**: `sdk/src/main/java/.../util/SchemaGenerator.java`

### Build Configuration
- **Root build**: `build.gradle.kts` - Versioning, multi-module setup
- **SDK build**: `sdk/build.gradle.kts` - Maven publishing, signing
- **Core build**: `core/build.gradle.kts` - ShadowJar, dependencies
- **Settings**: `settings.gradle.kts` - Module inclusion
- **Version file**: `version.properties` - Auto-generated version state