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
 * @param id           Unique event ID.
 * @param timestamp    Timestamp when the event occurred.
 * @param eventType    Type of audit event.
 * @param capabilityId ID of the capability that was executed.
 * @param caller       Caller information.
 * @param request      Request data (sanitized, without sensitive information).
 * @param response     Response data (sanitized, without sensitive information).
 * @param riskLevel    Risk level of the capability.
 * @param metadata     Additional metadata.
 * @param success      Whether the execution was successful.
 * @param error        Error information (if failed).
 * @author ZhangYuheng
 * @since 1.0.0
 */
public record AuditEvent(String id, Instant timestamp, AuditEventType eventType, String capabilityId, CallerInfo caller,
                         Map<String, Object> request, Object response, RiskLevel riskLevel,
                         Map<String, Object> metadata, boolean success, String error) {

    /**
     * Constructs a new AuditEvent.
     *
     * @param id           the event ID
     * @param timestamp    the timestamp
     * @param eventType    the event type
     * @param capabilityId the capability ID
     * @param caller       the caller info
     * @param request      the request data
     * @param response     the response data
     * @param riskLevel    the risk level
     * @param metadata     additional metadata
     * @param success      whether successful
     * @param error        error information
     */
    public AuditEvent {
    }

    /**
     * Gets the event ID.
     *
     * @return the event ID
     */
    @Override
    public String id() {
        return id;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    @Override
    public Instant timestamp() {
        return timestamp;
    }

    /**
     * Gets the event type.
     *
     * @return the event type
     */
    @Override
    public AuditEventType eventType() {
        return eventType;
    }

    /**
     * Gets the capability ID.
     *
     * @return the capability ID
     */
    @Override
    public String capabilityId() {
        return capabilityId;
    }

    /**
     * Gets the caller information.
     *
     * @return the caller info
     */
    @Override
    public CallerInfo caller() {
        return caller;
    }

    /**
     * Gets the request data.
     *
     * @return the request data
     */
    @Override
    public Map<String, Object> request() {
        return request;
    }

    /**
     * Gets the response data.
     *
     * @return the response data
     */
    @Override
    public Object response() {
        return response;
    }

    /**
     * Gets the risk level.
     *
     * @return the risk level
     */
    @Override
    public RiskLevel riskLevel() {
        return riskLevel;
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    @Override
    public Map<String, Object> metadata() {
        return metadata;
    }

    /**
     * Checks if the execution was successful.
     *
     * @return true if successful
     */
    @Override
    public boolean success() {
        return success;
    }

    /**
     * Gets the error information.
     *
     * @return the error
     */
    @Override
    public String error() {
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
