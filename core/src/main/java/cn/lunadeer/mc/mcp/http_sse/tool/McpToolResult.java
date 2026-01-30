package cn.lunadeer.mc.mcp.http_sse.tool;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MCP Tool Result.
 * <p>
 * Represents the result of a tool call.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 * @see <a href="https://modelcontextprotocol.io/specification/2025-11-25/server/tools#data-types">MCP Tools Data Types</a>
 */
public class McpToolResult {
    
    private static final Gson gson = new Gson();
    
    /**
     * Content of the result.
     */
    private final List<ContentItem> content;
    
    /**
     * Whether the tool call resulted in an error.
     */
    private final boolean isError;
    
    /**
     * Structured content (optional).
     */
    private final JsonElement structuredContent;
    
    public McpToolResult(List<ContentItem> content, boolean isError, JsonElement structuredContent) {
        this.content = content != null ? new ArrayList<>(content) : new ArrayList<>();
        this.isError = isError;
        this.structuredContent = structuredContent;
    }
    
    public List<ContentItem> getContent() {
        return new ArrayList<>(content);
    }
    
    public boolean isError() {
        return isError;
    }
    
    public JsonElement getStructuredContent() {
        return structuredContent;
    }
    
    /**
     * Converts the result to a JsonObject for JSON-RPC response.
     */
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        
        // Convert content items to JSON array
        JsonArray contentArray = new JsonArray();
        for (ContentItem item : content) {
            contentArray.add(item.toJsonObject());
        }
        json.add("content", contentArray);
        
        json.addProperty("isError", isError);
        
        if (structuredContent != null) {
            json.add("structuredContent", structuredContent);
        }
        
        return json;
    }
    
    /**
     * Factory method for creating a successful result with text content.
     */
    public static McpToolResult success(String text) {
        List<ContentItem> content = new ArrayList<>();
        content.add(new TextContent(text));
        return new McpToolResult(content, false, null);
    }
    
    /**
     * Factory method for creating an error result.
     */
    public static McpToolResult error(String message) {
        List<ContentItem> content = new ArrayList<>();
        content.add(new TextContent(message));
        return new McpToolResult(content, true, null);
    }
    
    /**
     * Factory method for creating a result with structured content.
     */
    public static McpToolResult successWithStructured(String text, JsonElement structuredContent) {
        List<ContentItem> content = new ArrayList<>();
        content.add(new TextContent(text));
        return new McpToolResult(content, false, structuredContent);
    }
    
    /**
     * Base class for content items.
     */
    public static abstract class ContentItem {
        protected final String type;
        
        protected ContentItem(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
        
        public abstract JsonObject toJsonObject();
    }
    
    /**
     * Text content item.
     */
    public static class TextContent extends ContentItem {
        private final String text;
        
        public TextContent(String text) {
            super("text");
            this.text = text;
        }
        
        public String getText() {
            return text;
        }
        
        @Override
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("text", text);
            return json;
        }
    }
    
    /**
     * Image content item.
     */
    public static class ImageContent extends ContentItem {
        private final String data;
        private final String mimeType;
        
        public ImageContent(String data, String mimeType) {
            super("image");
            this.data = data;
            this.mimeType = mimeType;
        }
        
        public String getData() {
            return data;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        @Override
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("data", data);
            json.addProperty("mimeType", mimeType);
            return json;
        }
    }
    
    /**
     * Audio content item.
     */
    public static class AudioContent extends ContentItem {
        private final String data;
        private final String mimeType;
        
        public AudioContent(String data, String mimeType) {
            super("audio");
            this.data = data;
            this.mimeType = mimeType;
        }
        
        public String getData() {
            return data;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        @Override
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("data", data);
            json.addProperty("mimeType", mimeType);
            return json;
        }
    }
    
    /**
     * Resource link content item.
     */
    public static class ResourceLinkContent extends ContentItem {
        private final String uri;
        private final String name;
        private final String description;
        private final String mimeType;
        
        public ResourceLinkContent(String uri, String name, String description, String mimeType) {
            super("resource_link");
            this.uri = uri;
            this.name = name;
            this.description = description;
            this.mimeType = mimeType;
        }
        
        public String getUri() {
            return uri;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        @Override
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("uri", uri);
            json.addProperty("name", name);
            if (description != null) {
                json.addProperty("description", description);
            }
            if (mimeType != null) {
                json.addProperty("mimeType", mimeType);
            }
            return json;
        }
    }
}
