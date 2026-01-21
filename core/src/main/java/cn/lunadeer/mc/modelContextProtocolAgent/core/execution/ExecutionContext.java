package cn.lunadeer.mc.modelContextProtocolAgent.core.execution;

import cn.lunadeer.mc.modelContextProtocolAgent.core.registry.CapabilityDescriptor;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpRequest;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpResponse;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.RiskLevel;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.CapabilityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for capability execution.
 * <p>
 * Contains all information needed during the execution lifecycle,
 * including request data, capability metadata, and execution state.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class ExecutionContext {

    /**
     * The original request.
     */
    private final McpRequest request;

    /**
     * The capability descriptor.
     */
    private final CapabilityDescriptor capability;

    /**
     * Caller information (permissions, roles, etc.).
     */
    private final CallerInfo caller;

    /**
     * Request parameters (parsed and validated).
     */
    private final Map<String, Object> parameters;

    /**
     * Execution metadata (for interceptors to store data).
     */
    private final Map<String, Object> metadata;

    /**
     * Whether execution was skipped.
     */
    private boolean skipped = false;

    /**
     * The result of execution.
     */
    private Object result;

    /**
     * The response to send back.
     */
    private McpResponse response;

    /**
     * Constructs a new ExecutionContext.
     *
     * @param request the MCP request
     * @param capability the capability descriptor
     * @param caller the caller information
     * @param parameters the parsed parameters
     */
    public ExecutionContext(McpRequest request, CapabilityDescriptor capability,
                           CallerInfo caller, Map<String, Object> parameters) {
        this.request = request;
        this.capability = capability;
        this.caller = caller;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.metadata = new HashMap<>();
    }

    /**
     * Gets the request.
     *
     * @return the request
     */
    public McpRequest getRequest() {
        return request;
    }

    /**
     * Gets the capability descriptor.
     *
     * @return the capability descriptor
     */
    public CapabilityDescriptor getCapability() {
        return capability;
    }

    /**
     * Gets the capability ID.
     *
     * @return the capability ID
     */
    public String getCapabilityId() {
        return capability != null ? capability.getId() : null;
    }

    /**
     * Gets the caller information.
     *
     * @return the caller info
     */
    public CallerInfo getCaller() {
        return caller;
    }

    /**
     * Gets the request parameters.
     *
     * @return the parameters map
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Gets a parameter value by name.
     *
     * @param name the parameter name
     * @return the parameter value, or null if not found
     */
    public Object getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Gets a parameter value by name with type casting.
     *
     * @param name the parameter name
     * @param type the expected type
     * @param <T> the type parameter
     * @return the parameter value, or null if not found or wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, Class<T> type) {
        Object value = parameters.get(name);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * Gets the execution metadata.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets a metadata value.
     *
     * @param key the metadata key
     * @param value the metadata value
     */
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    /**
     * Gets a metadata value.
     *
     * @param key the metadata key
     * @return the metadata value
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Checks if execution was skipped.
     *
     * @return true if skipped
     */
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * Sets whether execution was skipped.
     *
     * @param skipped true to skip execution
     */
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    /**
     * Gets the execution result.
     *
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * Sets the execution result.
     *
     * @param result the result
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    public McpResponse getResponse() {
        return response;
    }

    /**
     * Sets the response.
     *
     * @param response the response
     */
    public void setResponse(McpResponse response) {
        this.response = response;
    }

    /**
     * Gets the risk level.
     *
     * @return the risk level
     */
    public RiskLevel getRiskLevel() {
        return capability != null ? capability.getRiskLevel() : RiskLevel.LOW;
    }

    /**
     * Gets the capability type.
     *
     * @return the capability type
     */
    public CapabilityType getCapabilityType() {
        return capability != null ? capability.getType() : null;
    }

    /**
     * Gets the request ID.
     *
     * @return the request ID
     */
    public String getRequestId() {
        return request != null ? request.getId() : null;
    }
}
