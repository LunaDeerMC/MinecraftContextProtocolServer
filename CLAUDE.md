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

### Publish SDK to Maven Local
```bash
./gradlew :sdk:uploadToLocal
```

### Publish SDK to Maven Central
```bash
./gradlew :sdk:uploadToCentral -PcentralUsername=... -PcentralPassword=...
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
├── sdk/                      # Maven-published SDK library
│   ├── build.gradle.kts
│   └── src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/
│       ├── annotations/      # @McpProvider, @McpAction, @McpContext, @McpEvent, @Param
│       ├── api/              # McpAgent, McpProviderRegistry, McpEventEmitter
│       ├── model/            # CapabilityManifest, RiskLevel, CapabilityType, ErrorCode
│       └── util/             # JsonUtil, SchemaGenerator
└── core/                     # Minecraft plugin
    ├── build.gradle.kts
    └── src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/
        ├── ModelContextProtocolAgent.java    # Main plugin class
        ├── Configuration.java                # Static config class
        └── infrastructure/
            ├── configuration/                # YAML config management system
            │   ├── ConfigurationManager.java # Reflection-based YAML loader/saver
            │   ├── ConfigurationFile.java    # Base for config classes
            │   ├── ConfigurationPart.java    # Nested config sections
            │   ├── Comment/Comments.java     # Inline YAML comments
            │   ├── Headers.java              # File header comments
            │   ├── HandleManually.java       # Skip field annotation
            │   ├── PreProcess.java           # Pre-load hook
            │   └── PostProcess.java          # Post-load hook
            ├── scheduler/                     # Paper/Spigot task abstraction
            ├── XLogger.java                  # Debug/info/warn/error logging
            └── Notification.java             # Player/Console messaging
```

### Versioning System
- **Beta branch** (e.g., `main`): Version `1.0.0-beta`
- **Alpha branch** (e.g., `dev/*`): Version `1.0.0-alpha.N` (auto-incremented)
- Version is stored in `version.properties` and auto-updated by Gradle

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

1. **ModelContextProtocolAgent** (Main Plugin Class)
   - Entry point for plugin lifecycle
   - Initializes infrastructure (Logger, Scheduler, Notification)
   - Loads configuration on enable

2. **McpProviderRegistry** (SDK Interface)
   - Registers provider instances
   - Scans for `@McpContext`, `@McpAction`, `@McpEvent` annotations
   - Generates `CapabilityManifest` for each capability
   - Uses `SchemaGenerator` to create JSON schemas from Java types

3. **ConfigurationManager**
   - Reflection-based YAML loader/saver
   - Supports nested configuration sections
   - Preserves comments in YAML files
   - Pre/post-process hooks for validation/transformation

4. **Scheduler**
   - Abstraction over Paper/Spigot task scheduling
   - Supports Folia (async chunk operations)

5. **XLogger & Notification**
   - Unified logging with debug levels
   - Color-coded console output
   - Player message formatting

### WebSocket Server (Planned)
The plugin will host a WebSocket server for Gateway communication:
- Configurable host/port in `config.yml`
- Authentication via `authToken`
- Heartbeat mechanism for connection health
- Capability discovery and execution protocol

## Testing

### Running Tests
```bash
./gradlew test
```

### Integration Testing
Since this is a Minecraft plugin, integration tests require:
1. Build the plugin JAR
2. Install on a Paper/Spigot server
3. Test with actual Minecraft clients

## Common Development Tasks

### Adding a New Annotation to SDK
1. Add annotation class in `sdk/src/main/java/.../annotations/`
2. Update `CapabilityType` enum if needed
3. Update `SchemaGenerator` to handle new annotation
4. Update `CapabilityManifest` fields if needed
5. Bump SDK version in `sdk/build.gradle.kts`

### Adding a New Config Option
1. Add field to `Configuration.java` or nested `ConfigurationPart`
2. Add `@Comment` annotation for documentation
3. Plugin will auto-generate config on next load

### Debugging
1. Set `debug: true` in `config.yml`
2. Use `XLogger.debug()` for verbose logging
3. Check console for `D |` prefixed debug messages

## Important Notes

### Java Version
- Requires Java 17+ (configured in toolchain)
- Minecraft 1.20.1+ compatible

### Dependencies
- **Core**: Paper API 1.20.1, Adventure platform for Bukkit
- **SDK**: Paper API (compileOnly) for Bukkit types
- **Build**: Shadow plugin for fat JAR, RunPaper for dev server

### Folia Support
The plugin is marked as Folia-compatible in `plugin.yml`. The `Scheduler` abstraction handles async operations properly.

### Configuration File Location
- Main config: `plugins/ModelContextProtocolAgent/config.yml`
- Language files: `plugins/ModelContextProtocolAgent/languages/`

## File References

- **Main plugin class**: [core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/ModelContextProtocolAgent.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/ModelContextProtocolAgent.java)
- **Configuration definition**: [core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/Configuration.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/Configuration.java)
- **Config manager**: [core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/infrastructure/configuration/ConfigurationManager.java](core/src/main/java/cn/lunadeer/mc/modelContextProtocolAgent/infrastructure/configuration/ConfigurationManager.java)
- **SDK main interface**: [sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/api/McpAgent.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/api/McpAgent.java)
- **Annotations**: [sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/annotations/](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/annotations/)
- **Capability manifest**: [sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/model/CapabilityManifest.java](sdk/src/main/java/cn/lunadeer/mc/modelContextProtocolAgentSDK/model/CapabilityManifest.java)
