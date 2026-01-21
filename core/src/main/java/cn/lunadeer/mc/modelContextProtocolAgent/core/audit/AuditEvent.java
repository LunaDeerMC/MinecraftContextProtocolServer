package cn.lunadeer.mc.modelContextProtocolAgent.core.audit;

import cn.lunadeer.mc.modelContextProtocolAgent.core.execution.CallerInfo;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.RiskLevel;

import java.time.Instant;
import java.util.Map;

/**
 * Audit event for capability execution.
 * <p>
 * Contains all information about a capability execution for auditing purposes.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class AuditEvent {

    /**
     * Unique event ID.
     */
    private final String id;

    /**
     * Timestamp when the event occurred.
     */
    private final Instant timestamp;

    /**
     * Type of audit event.
     */
    private final AuditEventType eventType;

    /**
     * ID of the capability that was executed.
     */
    private final String capabilityId;

    /**
     * Caller information.
     */
    private final CallerInfo caller;

    /**
     * Request data (sanitized, without sensitive information).
     */
    private final Map<String, Object> request;

    /**
     * Response data (sanitized, without sensitive information).
     */
    private final Object response;

    /**
     * Risk level of the capability.
     */
    private final RiskLevel riskLevel;

    /**
     * Additional metadata.
     */
    private final Map<String, Object> metadata;

    /**
     * Whether the execution was successful.
     */
    private final boolean success;

    /**
     * Error information (if failed).
     */
    private final String error;

    /**
     * Constructs a new AuditEvent.
     *
     * @param id the event ID
     * @param timestamp the timestamp
     * @param eventType the event type
     * @param capabilityId the capability ID
     * @param caller the caller info
     * @param request the request data
     * @param response the response data
     * @param riskLevel the risk level
     * @param metadata additional metadata
     * @param success whether successful
     * @param error error information
     */
    public AuditEvent(String id, Instant timestamp, AuditEventType eventType, String capabilityId,
                     CallerInfo caller, Map<String, Object> request, Object response,
                     RiskLevel riskLevel, Map<String, Object> metadata, boolean success, String error) {
        this.id = id;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.capabilityId = capabilityId;
        this.caller = caller;
        this.request = request;
        this.response = response;
        this.riskLevel = riskLevel;
        this.metadata = metadata;
        this.success = success;
        this.error = error;
    }

    /**
     * Gets the event ID.
     *
     * @return the event ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the event type.
     *
     * @return the event type
     */
    public AuditEventType getEventType() {
        return eventType;
    }

    /**
     * Gets the capability ID.
     *
     * @return the capability ID
     */
    public String getCapabilityId() {
        return capabilityId;
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
     * Gets the request data.
     *
     * @return the request data
     */
    public Map<String, Object> getRequest() {
        return request;
    }

    /**
     * Gets the response data.
     *
     * @return the response data
     */
    public Object getResponse() {
        return response;
    }

    /**
     * Gets the risk level.
     *
     * @return the risk level
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Checks if the execution was successful.
     *
     * @return true if successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the error information.
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * Builder for AuditEvent.
     */
    public static class Builder {
        private String id;
        private Instant timestamp;
        private AuditEventType eventType;
        private String capabilityId;
        private CallerInfo caller;
        private Map<String, Object> request;
        private Object response;
        private RiskLevel riskLevel;
        private Map<String, Object> metadata;
        private boolean success;
        private String error;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder eventType(AuditEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder capabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
            return this;
        }

        public Builder caller(CallerInfo caller) {
            this.caller = caller;
            return this;
        }

        public Builder request(Map<String, Object> request) {
            this.request = request;
            return this;
        }

        public Builder response(Object response) {
            this.response = response;
            return this;
        }

        public Builder riskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public AuditEvent build() {
            return new AuditEvent(id, timestamp, eventType, capabilityId, caller,
                    request, response, riskLevel, metadata, success, error);
        }
    }
}
