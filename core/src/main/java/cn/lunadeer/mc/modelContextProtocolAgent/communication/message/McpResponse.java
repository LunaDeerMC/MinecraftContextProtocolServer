package cn.lunadeer.mc.modelContextProtocolAgent.communication.message;

import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.ErrorCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

/**
 * MCP response message for capability execution results.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpResponse extends McpMessage {
    private final boolean success;
    private final ErrorCode errorCode;
    private final String errorMessage;
    private final Object data;
    private final Map<String, Object> details;

    private McpResponse(Builder builder) {
        super(builder.id, "response");
        this.success = builder.success;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
        this.data = builder.data;
        this.details = builder.details;
    }

    public boolean isSuccess() {
        return success;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getData() {
        return data;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    @Override
    public JsonElement getPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty("success", success);

        if (!success) {
            if (errorCode != null) {
                payload.addProperty("errorCode", errorCode.name());
            }
            if (errorMessage != null) {
                payload.addProperty("errorMessage", errorMessage);
            }
            if (details != null && !details.isEmpty()) {
                JsonObject detailsObj = new JsonObject();
                for (Map.Entry<String, Object> entry : details.entrySet()) {
                    if (entry.getValue() instanceof String) {
                        detailsObj.addProperty(entry.getKey(), (String) entry.getValue());
                    } else if (entry.getValue() instanceof Number) {
                        detailsObj.addProperty(entry.getKey(), (Number) entry.getValue());
                    } else if (entry.getValue() instanceof Boolean) {
                        detailsObj.addProperty(entry.getKey(), (Boolean) entry.getValue());
                    }
                }
                payload.add("details", detailsObj);
            }
        } else {
            if (data != null) {
                Gson gson = new GsonBuilder().create();
                payload.add("data", JsonParser.parseString(gson.toJson(data)));
            }
        }

        return payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder success(String id, Object data) {
        return new Builder()
            .id(id)
            .success(true)
            .data(data);
    }

    public static Builder error(String id, ErrorCode errorCode, String errorMessage) {
        return new Builder()
            .id(id)
            .success(false)
            .errorCode(errorCode)
            .errorMessage(errorMessage);
    }

    public static Builder error(String id, ErrorCode errorCode, String errorMessage, Map<String, Object> details) {
        return new Builder()
            .id(id)
            .success(false)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .details(details);
    }

    public static class Builder {
        private String id;
        private boolean success = true;
        private ErrorCode errorCode;
        private String errorMessage;
        private Object data;
        private Map<String, Object> details;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }

        public McpResponse build() {
            return new McpResponse(this);
        }
    }
}
