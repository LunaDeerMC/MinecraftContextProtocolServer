package cn.lunadeer.mc.modelContextProtocolAgent.http_sse.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * MCP Tool Call Request.
 * <p>
 * Represents a request to call a tool with specific arguments.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 * @see <a href="https://modelcontextprotocol.io/specification/2025-11-25/server/tools#protocol-messages">MCP Tools Protocol Messages</a>
 */
public class McpToolRequest {
    
    /**
     * The name of the tool to call.
     */
    private final String name;
    
    /**
     * The arguments to pass to the tool.
     */
    private final JsonObject arguments;
    
    public McpToolRequest(String name, JsonObject arguments) {
        this.name = name;
        this.arguments = arguments != null ? arguments : new JsonObject();
    }
    
    public String getName() {
        return name;
    }
    
    public JsonObject getArguments() {
        return arguments;
    }
    
    /**
     * Parses a tool call request from a JSON-RPC request.
     */
    public static McpToolRequest fromJsonRpcRequest(JsonObject requestParams) {
        String name = requestParams.get("name").getAsString();
        JsonObject arguments = requestParams.getAsJsonObject("arguments");
        return new McpToolRequest(name, arguments);
    }
    
    /**
     * Converts the request to a JsonObject for validation/logging.
     */
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.add("arguments", arguments);
        return json;
    }
}
