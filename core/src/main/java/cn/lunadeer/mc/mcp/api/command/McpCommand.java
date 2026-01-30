package cn.lunadeer.mc.mcp.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for MCP Server admin commands.
 * <p>
 * Provides common functionality for command handling and tab completion.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public abstract class McpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return execute(sender, args);
        } catch (Exception e) {
            sender.sendMessage("§cError executing command: " + e.getMessage());
            return false;
        }
    }

    /**
     * Executes the command.
     *
     * @param sender the command sender
     * @param args   the command arguments
     * @return true if the command was handled successfully
     */
    protected abstract boolean execute(CommandSender sender, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return getTabCompletions(sender, args);
    }

    /**
     * Gets tab completions for the command.
     *
     * @param sender the command sender
     * @param args   the current arguments
     * @return list of possible completions
     */
    protected List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    /**
     * Checks if the sender has the required permission.
     *
     * @param sender     the command sender
     * @param permission the permission node
     * @return true if the sender has the permission
     */
    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    /**
     * Sends a message to the sender.
     *
     * @param sender  the command sender
     * @param message the message to send
     */
    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage("§7[§bMCP§7] " + message);
    }

    /**
     * Sends an error message to the sender.
     *
     * @param sender  the command sender
     * @param message the error message
     */
    protected void sendError(CommandSender sender, String message) {
        sender.sendMessage("§c[§bMCP§c] " + message);
    }

    /**
     * Sends a success message to the sender.
     *
     * @param sender  the command sender
     * @param message the success message
     */
    protected void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage("§a[§bMCP§a] " + message);
    }
}
