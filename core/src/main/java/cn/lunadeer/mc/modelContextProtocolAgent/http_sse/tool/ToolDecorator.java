package cn.lunadeer.mc.modelContextProtocolAgent.http_sse.tool;

import cn.lunadeer.mc.modelContextProtocolAgent.core.registry.CapabilityDescriptor;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.XLogger;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.CapabilityManifest;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.CapabilityType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool Decorator - Converts Capability to MCP Tool format.
 * <p>
 * This class acts as a compatibility layer that transforms internal
 * Capability descriptors into MCP Tool definitions according to the
 * MCP protocol specification.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 * @see <a href="https://modelcontextprotocol.io/specification/2025-11-25/server/tools#data-types">MCP Tools Data Types</a>
 */
public class ToolDecorator {
    
    private static final Gson gson = new Gson();
    
    /**
     * Converts a CapabilityDescriptor to an McpTool.
     *
     * @param descriptor the capability descriptor
     * @return the MCP tool definition
     */
    public static McpTool decorate(CapabilityDescriptor descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("CapabilityDescriptor cannot be null");
        }
        
        CapabilityManifest manifest = descriptor.getManifest();
        
        // Validate tool name according to MCP specification
        String toolName = validateToolName(manifest.getId());
        
        // Build tool title
        String title = manifest.getName();
        if (title == null || title.isEmpty()) {
            title = toolName;
        }
        
        // Build description
        String description = manifest.getDescription();
        if (description == null || description.isEmpty()) {
            description = "No description available";
        }
        
        // Build tool annotations
        McpTool.ToolAnnotations annotations = buildAnnotations(descriptor);
        
        // Build icons (optional)
        List<McpTool.Icon> icons = buildIcons(descriptor);
        
        return new McpTool(
                toolName,
                title,
                description,
                icons,
                descriptor.getParameterSchema(),
                descriptor.getReturnSchema(),
                annotations
        );
    }
    
    /**
     * Converts multiple CapabilityDescriptors to a list of McpTools.
     *
     * @param descriptors the capability descriptors
     * @return the list of MCP tools
     */
    public static List<McpTool> decorateAll(List<CapabilityDescriptor> descriptors) {
        List<McpTool> tools = new ArrayList<>();
        for (CapabilityDescriptor descriptor : descriptors) {
            try {
                tools.add(decorate(descriptor));
            } catch (Exception e) {
                XLogger.warn("Failed to decorate capability: " + descriptor.getId(), e);
            }
        }
        return tools;
    }
    
    /**
     * Validates and normalizes tool names according to MCP specification.
     * <p>
     * Tool names SHOULD be between 1 and 128 characters in length (inclusive).
     * Tool names SHOULD be considered case-sensitive.
     * The following SHOULD be the only allowed characters: uppercase and lowercase ASCII letters (A-Z, a-z), digits (0-9), underscore (_), hyphen (-), and dot (.)
     * Tool names SHOULD NOT contain spaces, commas, or other special characters.
     * Tool names SHOULD be unique within a server.
     * </p>
     */
    private static String validateToolName(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be null or empty");
        }
        
        if (id.length() < 1 || id.length() > 128) {
            throw new IllegalArgumentException("Tool name must be between 1 and 128 characters: " + id);
        }
        
        // Check for valid characters: A-Z, a-z, 0-9, _, -, .
        if (!id.matches("^[A-Za-z0-9_.-]+$")) {
            throw new IllegalArgumentException("Tool name contains invalid characters. Only A-Z, a-z, 0-9, _, -, and . are allowed: " + id);
        }
        
        return id;
    }
    
    /**
     * Builds tool annotations from capability descriptor.
     */
    private static McpTool.ToolAnnotations buildAnnotations(CapabilityDescriptor descriptor) {
        CapabilityType type = descriptor.getType();
        
        // Based on capability type, set appropriate annotations
        boolean readOnlyHint = type == CapabilityType.CONTEXT;
        boolean destructiveHint = type == CapabilityType.ACTION;
        boolean idempotentHint = type != CapabilityType.ACTION; // CONTEXT and EVENT are idempotent
        McpTool.ToolAnnotations.OpenWorldHint openWorldHint = McpTool.ToolAnnotations.OpenWorldHint.UNKNOWN;
        boolean localOnlyHint = true; // Most Minecraft capabilities are local
        
        return new McpTool.ToolAnnotations(
                readOnlyHint,
                destructiveHint,
                idempotentHint,
                openWorldHint,
                localOnlyHint
        );
    }
    
    /**
     * Builds tool icons from capability descriptor.
     */
    private static List<McpTool.Icon> buildIcons(CapabilityDescriptor descriptor) {
        List<McpTool.Icon> icons = new ArrayList<>();
        
        // Add default icon based on capability type
        String iconSrc;
        CapabilityType type = descriptor.getType();
        
        switch (type) {
            case CONTEXT:
                iconSrc = "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><text y='20' font-size='20'>🔍</text></svg>";
                break;
            case ACTION:
                iconSrc = "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><text y='20' font-size='20'>⚡</text></svg>";
                break;
            case EVENT:
                iconSrc = "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><text y='20' font-size='20'>🔔</text></svg>";
                break;
            default:
                iconSrc = "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><text y='20' font-size='20'>⚙️</text></svg>";
                break;
        }
        
        icons.add(new McpTool.Icon(iconSrc, "image/svg+xml", List.of("24x24")));
        
        return icons;
    }
    
    /**
     * Converts a tool call request to a capability invocation.
     * <p>
     * This method transforms MCP Tool call format to internal capability invocation format.
     * </p>
     */
    public static Map<String, Object> convertToolCallToCapability(
            McpToolRequest toolRequest,
            CapabilityDescriptor descriptor
    ) {
        Map<String, Object> invocationParams = new HashMap<>();
        
        // Map tool arguments to capability parameters
        JsonObject arguments = toolRequest.getArguments();
        if (arguments != null && !arguments.entrySet().isEmpty()) {
            arguments.entrySet().forEach(entry -> {
                invocationParams.put(entry.getKey(), gson.fromJson(entry.getValue(), Object.class));
            });
        }
        
        return invocationParams;
    }
    
    /**
     * Converts a capability result to a tool result.
     * <p>
     * This method transforms internal capability result to MCP Tool result format.
     * </p>
     */
    public static McpToolResult convertCapabilityToToolResult(
            Object capabilityResult,
            CapabilityDescriptor descriptor
    ) {
        // Convert capability result to appropriate format
        if (capabilityResult instanceof String) {
            return McpToolResult.success((String) capabilityResult);
        } else if (capabilityResult instanceof Map) {
            // Handle structured results
            JsonObject structuredContent = gson.toJsonTree(capabilityResult).getAsJsonObject();
            String text = gson.toJson(capabilityResult);
            return McpToolResult.successWithStructured(text, structuredContent);
        } else if (capabilityResult instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) capabilityResult;
            String text = gson.toJson(jsonObject);
            return McpToolResult.successWithStructured(text, jsonObject);
        } else {
            // Default: convert to string
            String text = capabilityResult != null ? capabilityResult.toString() : "Success";
            return McpToolResult.success(text);
        }
    }
}
