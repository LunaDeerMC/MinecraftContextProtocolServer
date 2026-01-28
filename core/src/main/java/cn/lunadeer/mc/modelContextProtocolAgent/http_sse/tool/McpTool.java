package cn.lunadeer.mc.modelContextProtocolAgent.http_sse.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP Tool definition as per MCP protocol specification.
 * <p>
 * Represents a tool that can be invoked by the client.
 * Follows the MCP Tool data type specification.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 * @see <a href="https://modelcontextprotocol.io/specification/2025-11-25/server/tools#data-types">MCP Tools Specification</a>
 */
public class McpTool {
    
    /**
     * Unique identifier for the tool.
     * Tool names SHOULD be between 1 and 128 characters in length (inclusive).
     * Tool names SHOULD be considered case-sensitive.
     * The following SHOULD be the only allowed characters: uppercase and lowercase ASCII letters (A-Z, a-z), digits (0-9), underscore (_), hyphen (-), and dot (.)
     * Tool names SHOULD NOT contain spaces, commas, or other special characters.
     * Tool names SHOULD be unique within a server.
     */
    private final String name;
    
    /**
     * Optional human-readable name of the tool for display purposes.
     */
    private final String title;
    
    /**
     * Human-readable description of functionality.
     */
    private final String description;
    
    /**
     * Optional array of icons for display in user interfaces.
     */
    private final List<Icon> icons;
    
    /**
     * JSON Schema defining expected parameters.
     * Follows the JSON Schema usage guidelines.
     * Defaults to 2020-12 if no $schema field is present.
     * MUST be a valid JSON Schema object (not null).
     * For tools with no parameters, use one of these valid approaches:
     * - { "type": "object", "additionalProperties": false } - Recommended: explicitly accepts only empty objects
     * - { "type": "object" } - accepts any object (including with properties)
     */
    private final Map<String, Object> inputSchema;
    
    /**
     * Optional JSON Schema defining expected output structure.
     */
    private final Map<String, Object> outputSchema;
    
    /**
     * Optional properties describing tool behavior.
     */
    private final ToolAnnotations annotations;
    
    public McpTool(
            String name,
            String title,
            String description,
            List<Icon> icons,
            Map<String, Object> inputSchema,
            Map<String, Object> outputSchema,
            ToolAnnotations annotations
    ) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.icons = icons != null ? new ArrayList<>(icons) : new ArrayList<>();
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.annotations = annotations;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Icon> getIcons() {
        return new ArrayList<>(icons);
    }
    
    public Map<String, Object> getInputSchema() {
        return inputSchema;
    }
    
    public Map<String, Object> getOutputSchema() {
        return outputSchema;
    }
    
    public ToolAnnotations getAnnotations() {
        return annotations;
    }
    
    /**
     * Converts the tool to a JsonObject for JSON-RPC response.
     */
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        
        if (title != null && !title.isEmpty()) {
            json.addProperty("title", title);
        }
        
        if (description != null && !description.isEmpty()) {
            json.addProperty("description", description);
        }
        
        if (!icons.isEmpty()) {
            // Convert icons to JSON array
            // Implementation depends on Icon structure
        }
        
        if (inputSchema != null) {
            // Convert inputSchema to JSON
            // This will be handled by Gson
            Gson gson = new GsonBuilder().create();
            json.add("inputSchema", JsonParser.parseString(gson.toJson(inputSchema)));
        }
        
        if (outputSchema != null) {
            Gson gson = new GsonBuilder().create();
            json.add("outputSchema", JsonParser.parseString(gson.toJson(outputSchema)));
        }
        
        if (annotations != null) {
            json.add("annotations", annotations.toJsonObject());
        }
        
        return json;
    }
    
    /**
     * Icon definition for tool display.
     */
    public static class Icon {
        private final String src;
        private final String mimeType;
        private final List<String> sizes;
        
        public Icon(String src, String mimeType, List<String> sizes) {
            this.src = src;
            this.mimeType = mimeType;
            this.sizes = sizes != null ? new ArrayList<>(sizes) : new ArrayList<>();
        }
        
        public String getSrc() {
            return src;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public List<String> getSizes() {
            return new ArrayList<>(sizes);
        }
        
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("src", src);
            json.addProperty("mimeType", mimeType);
            
            if (!sizes.isEmpty()) {
                // Convert sizes to JSON array
                // Implementation depends on Gson usage
            }
            
            return json;
        }
    }
    
    /**
     * Tool annotations describing tool behavior.
     */
    public static class ToolAnnotations {
        private final boolean readOnlyHint;
        private final boolean destructiveHint;
        private final boolean idempotentHint;
        private final OpenWorldHint openWorldHint;
        private final boolean localOnlyHint;
        
        public ToolAnnotations(
                boolean readOnlyHint,
                boolean destructiveHint,
                boolean idempotentHint,
                OpenWorldHint openWorldHint,
                boolean localOnlyHint
        ) {
            this.readOnlyHint = readOnlyHint;
            this.destructiveHint = destructiveHint;
            this.idempotentHint = idempotentHint;
            this.openWorldHint = openWorldHint;
            this.localOnlyHint = localOnlyHint;
        }
        
        public boolean isReadOnlyHint() {
            return readOnlyHint;
        }
        
        public boolean isDestructiveHint() {
            return destructiveHint;
        }
        
        public boolean isIdempotentHint() {
            return idempotentHint;
        }
        
        public OpenWorldHint getOpenWorldHint() {
            return openWorldHint;
        }
        
        public boolean isLocalOnlyHint() {
            return localOnlyHint;
        }
        
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("readOnlyHint", readOnlyHint);
            json.addProperty("destructiveHint", destructiveHint);
            json.addProperty("idempotentHint", idempotentHint);
            json.addProperty("localOnlyHint", localOnlyHint);
            
            if (openWorldHint != null) {
                json.addProperty("openWorldHint", openWorldHint.toString());
            }
            
            return json;
        }
        
        public enum OpenWorldHint {
            OPEN_WORLD,
            RESTRICTED,
            UNKNOWN
        }
    }
}
