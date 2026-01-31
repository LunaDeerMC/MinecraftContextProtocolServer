package cn.lunadeer.mc.mcp.builtin_provider;

import cn.lunadeer.mc.mcp.sdk.annotations.McpProvider;
import cn.lunadeer.mc.mcp.sdk.annotations.McpTool;
import cn.lunadeer.mc.mcp.sdk.annotations.Param;
import cn.lunadeer.mc.mcp.sdk.exception.McpBusinessException;
import cn.lunadeer.mc.mcp.sdk.model.ErrorCode;
import cn.lunadeer.mc.mcp.sdk.model.RiskLevel;
import cn.lunadeer.mc.mcp.sdk.model.dto.TpsResult;
import cn.lunadeer.mc.mcp.sdk.model.dto.world.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Built-in MCP provider for world-related capabilities.
 * <p>
 * Provides capabilities for querying and modifying world state including time,
 * weather, game rules, and server performance metrics.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
@McpProvider(
        id = "mcp-internal-world",
        name = "MCP World Provider",
        version = "1.0.0",
        description = "Built-in capabilities for Minecraft world management"
)
public class WorldProvider {

    /**
     * Gets the current time of a world.
     *
     * @param worldName the name of the world
     * @return the world time result
     */
    @McpTool(
            id = "world.time.get",
            name = "Get World Time",
            description = "Retrieves the current time of a world",
            type = McpTool.ToolType.CONTEXT,
            permissions = {"mcp.context.world.time"},
            tags = {"world", "time", "query"}
    )
    public WorldTimeResult getWorldTime(
            @Param(name = "worldName", required = true, description = "The name of the world")
            String worldName
    ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "World not found: " + worldName
            );
        }

        long time = world.getTime();
        return new WorldTimeResult(
                worldName,
                time,
                world.getFullTime(),
                (int) (world.getFullTime() / 24000),
                time < 12000 ? WorldTimeResult.TimePhase.DAY : WorldTimeResult.TimePhase.NIGHT
        );
    }

    /**
     * Sets the time of a world.
     *
     * @param worldName the name of the world
     * @param time      the time to set (0-24000)
     * @param reason    optional reason for the change
     * @return the set time result
     */
    @McpTool(
            id = "world.time.set",
            name = "Set World Time",
            description = "Sets the current time of a world",
            type = McpTool.ToolType.ACTION,
            risk = RiskLevel.HIGH,
            snapshotRequired = true,
            rollbackSupported = true,
            permissions = {"mcp.action.world.time"},
            tags = {"world", "time", "modify"}
    )
    public SetTimeResult setWorldTime(
            @Param(name = "worldName", required = true, description = "The name of the world")
            String worldName,
            @Param(name = "time", required = true, description = "The time to set (0-24000)", min = 0, max = 24000)
            Long time,
            @Param(name = "reason", description = "Reason for the change")
            String reason
    ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "World not found: " + worldName
            );
        }

        long previousTime = world.getTime();
        world.setTime(time);

        return new SetTimeResult(true, previousTime, time);
    }

    /**
     * Gets the current weather of a world.
     *
     * @param worldName the name of the world
     * @return the weather result
     */
    @McpTool(
            id = "world.weather.get",
            name = "Get Weather",
            description = "Retrieves the current weather of a world",
            type = McpTool.ToolType.CONTEXT,
            permissions = {"mcp.context.world.weather"},
            tags = {"world", "weather", "query"}
    )
    public WeatherResult getWeather(
            @Param(name = "worldName", required = true, description = "The name of the world")
            String worldName
    ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "World not found: " + worldName
            );
        }

        WeatherType type;
        if (world.hasStorm()) {
            if (world.isThundering()) {
                type = WeatherType.THUNDER;
            } else {
                type = WeatherType.RAIN;
            }
        } else {
            type = WeatherType.CLEAR;
        }

        return new WeatherResult(worldName, type, world.getWeatherDuration());
    }

    /**
     * Sets the weather of a world.
     *
     * @param worldName the name of the world
     * @param type      the weather type to set
     * @param duration  optional duration in ticks
     * @return the set weather result
     */
    @McpTool(
            id = "world.weather.set",
            name = "Set Weather",
            description = "Sets the weather of a world",
            type = McpTool.ToolType.ACTION,
            risk = RiskLevel.MEDIUM,
            permissions = {"mcp.action.world.weather"},
            tags = {"world", "weather", "modify"}
    )
    public SetWeatherResult setWeather(
            @Param(name = "worldName", required = true, description = "The name of the world")
            String worldName,
            @Param(name = "type", required = true, description = "The weather type to set")
            WeatherType type,
            @Param(name = "duration", description = "Duration in ticks")
            Integer duration
    ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "World not found: " + worldName
            );
        }

        WeatherType previousType;
        if (world.hasStorm()) {
            if (world.isThundering()) {
                previousType = WeatherType.THUNDER;
            } else {
                previousType = WeatherType.RAIN;
            }
        } else {
            previousType = WeatherType.CLEAR;
        }

        // Set weather
        switch (type) {
            case CLEAR:
                world.setStorm(false);
                world.setThundering(false);
                break;
            case RAIN:
                world.setStorm(true);
                world.setThundering(false);
                break;
            case THUNDER:
                world.setStorm(true);
                world.setThundering(true);
                break;
        }

        if (duration != null) {
            world.setWeatherDuration(duration);
        }

        return new SetWeatherResult(true, previousType, type);
    }

    /**
     * Gets the TPS (Ticks Per Second) of the server.
     *
     * @return the TPS result
     */
    @McpTool(
            id = "world.tps.get",
            name = "Get TPS",
            description = "Retrieves the server's TPS (Ticks Per Second) metrics",
            type = McpTool.ToolType.CONTEXT,
            permissions = {"mcp.context.world.tps"},
            tags = {"world", "performance", "query"}
    )
    public TpsResult getTps() {
        double[] tps = Bukkit.getTPS();
        return new TpsResult(
                tps.length > 0 ? tps[0] : null,
                tps.length > 1 ? tps[1] : null,
                tps.length > 2 ? tps[2] : null,
                Bukkit.getAverageTickTime()
        );
    }

    /**
     * Gets a game rule value from a world.
     *
     * @param worldName the name of the world
     * @param rule      the game rule name
     * @return the game rule value
     */
    @McpTool(
            id = "world.rule.get",
            name = "Get Game Rule",
            description = "Retrieves a game rule value from a world",
            type = McpTool.ToolType.CONTEXT,
            permissions = {"mcp.context.world.rule"},
            tags = {"world", "rule", "query"}
    )
    public String getGameRule(
            @Param(name = "worldName", required = true, description = "The name of the world")
            String worldName,
            @Param(name = "rule", required = true, description = "The game rule name")
            String rule
    ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "World not found: " + worldName
            );
        }

        String value = world.getGameRuleValue(rule);
        if (value == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "Game rule not found: " + rule
            );
        }

        return value;
    }

    /**
     * Sets a game rule value in a world.
     *
     * @param worldName the name of the world
     * @param rule      the game rule name
     * @param value     the game rule value
     * @return true if successful
     */
    @McpTool(
            id = "world.rule.set",
            name = "Set Game Rule",
            description = "Sets a game rule value in a world",
            type = McpTool.ToolType.ACTION,
            risk = RiskLevel.HIGH,
            snapshotRequired = true,
            rollbackSupported = true,
            permissions = {"mcp.action.world.rule"},
            tags = {"world", "rule", "modify"}
    )
    public Boolean setGameRule(
            @Param(name = "worldName", required = true, description = "The name of the world")
            String worldName,
            @Param(name = "rule", required = true, description = "The game rule name")
            String rule,
            @Param(name = "value", required = true, description = "The game rule value")
            String value
    ) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new McpBusinessException(
                    ErrorCode.OPERATION_FAILED.getErrorCode(),
                    "World not found: " + worldName
            );
        }

        return world.setGameRuleValue(rule, value);
    }
}
