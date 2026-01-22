package cn.lunadeer.mc.modelContextProtocolAgent.core.schema;

import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.XLogger;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.exception.McpValidationException;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.model.ErrorCode;

import java.util.List;
import java.util.Map;

/**
 * Schema Validator for MCP capability parameters and return values.
 * <p>
 * Validates parameters against JSON Schema definitions.
 * This is a simplified implementation that validates basic schema constraints.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class SchemaValidator {

    /**
     * Validates request parameters against the capability's parameter schema.
     *
     * @param capabilityId the capability ID
     * @param parameterSchema the parameter schema (JSON Schema format)
     * @param parameters the parameters to validate
     * @throws McpValidationException if validation fails
     */
    public void validateParameters(String capabilityId,
                                   Map<String, Object> parameterSchema,
                                   Map<String, Object> parameters) throws McpValidationException {
        if (parameterSchema == null || parameterSchema.isEmpty()) {
            return; // No schema to validate against
        }

        try {
            // Validate required parameters
            List<?> requiredList = (List<?>) parameterSchema.get("required");
            if (requiredList != null) {
                for (Object requiredParam : requiredList) {
                    String paramName = requiredParam.toString();
                    if (!parameters.containsKey(paramName)) {
                        throw new McpValidationException(
                                ErrorCode.PARAMETER_REQUIRED.getErrorCode(),
                                "Required parameter '" + paramName + "' is missing for capability: " + capabilityId
                        );
                    }
                }
            }

            // Validate parameter types and constraints
            Map<?, ?> properties = (Map<?, ?>) parameterSchema.get("properties");
            if (properties != null) {
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String paramName = entry.getKey().toString();
                    Map<?, ?> paramSchema = (Map<?, ?>) entry.getValue();

                    if (parameters.containsKey(paramName)) {
                        validateParameterType(paramName, parameters.get(paramName), paramSchema);
                    }
                }
            }

            XLogger.debug("Parameters validated successfully for capability: " + capabilityId);

        } catch (McpValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            XLogger.error("Error during parameter validation for capability: " + capabilityId, ex);
            throw new McpValidationException(
                    ErrorCode.SCHEMA_VALIDATION_FAILED.getErrorCode(),
                    "Error during parameter validation: " + ex.getMessage()
            );
        }
    }

    /**
     * Validates return value against the capability's return schema.
     *
     * @param capabilityId the capability ID
     * @param returnSchema the return schema (JSON Schema format)
     * @param returnValue the return value to validate
     * @throws McpValidationException if validation fails
     */
    public void validateReturn(String capabilityId,
                               Map<String, Object> returnSchema,
                               Object returnValue) throws McpValidationException {
        if (returnSchema == null || returnSchema.isEmpty()) {
            return; // No schema to validate against
        }

        try {
            // Validate return type
            String expectedType = (String) returnSchema.get("type");
            if (expectedType != null && returnValue != null) {
                validateType("return value", returnValue, expectedType);
            }

            XLogger.debug("Return value validated successfully for capability: " + capabilityId);

        } catch (McpValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            XLogger.error("Error during return value validation for capability: " + capabilityId, ex);
            throw new McpValidationException(
                    ErrorCode.SCHEMA_VALIDATION_FAILED.getErrorCode(),
                    "Error during return value validation: " + ex.getMessage()
            );
        }
    }

    /**
     * Validates a single parameter's type and constraints.
     *
     * @param paramName the parameter name
     * @param paramValue the parameter value
     * @param paramSchema the parameter schema
     */
    private void validateParameterType(String paramName, Object paramValue, Map<?, ?> paramSchema) {
        String type = (String) paramSchema.get("type");
        if (type != null) {
            validateType(paramName, paramValue, type);
        }

        // Validate minimum/maximum for numbers
        if (paramValue instanceof Number) {
            Number number = (Number) paramValue;
            Double minimum = getDouble(paramSchema.get("minimum"));
            Double maximum = getDouble(paramSchema.get("maximum"));

            if (minimum != null && number.doubleValue() < minimum) {
                throw new McpValidationException(
                        ErrorCode.PARAMETER_INVALID.getErrorCode(),
                        "Parameter '" + paramName + "' must be at least " + minimum
                );
            }

            if (maximum != null && number.doubleValue() > maximum) {
                throw new McpValidationException(
                        ErrorCode.PARAMETER_INVALID.getErrorCode(),
                        "Parameter '" + paramName + "' must be at most " + maximum
                );
            }
        }

        // Validate pattern for strings
        if (paramValue instanceof String) {
            String pattern = (String) paramSchema.get("pattern");
            if (pattern != null && !pattern.isEmpty()) {
                // Simple pattern validation (basic regex check)
                try {
                    if (!((String) paramValue).matches(pattern)) {
                        throw new McpValidationException(
                                ErrorCode.PARAMETER_INVALID.getErrorCode(),
                                "Parameter '" + paramName + "' does not match pattern: " + pattern
                        );
                    }
                } catch (Exception ex) {
                    XLogger.warn("Invalid regex pattern for parameter '" + paramName + "': " + pattern);
                }
            }
        }
    }

    /**
     * Validates that a value matches the expected type.
     *
     * @param name the value name (for error messages)
     * @param value the value to validate
     * @param expectedType the expected JSON type
     */
    private void validateType(String name, Object value, String expectedType) {
        switch (expectedType) {
            case "string":
                if (!(value instanceof String)) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be a string"
                    );
                }
                break;
            case "integer":
                if (!(value instanceof Number)) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be an integer"
                    );
                }
                break;
            case "number":
                if (!(value instanceof Number)) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be a number"
                    );
                }
                break;
            case "boolean":
                if (!(value instanceof Boolean)) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be a boolean"
                    );
                }
                break;
            case "array":
                if (!(value instanceof List)) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be an array"
                    );
                }
                break;
            case "object":
                if (!(value instanceof Map)) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be an object"
                    );
                }
                break;
            case "null":
                if (value != null) {
                    throw new McpValidationException(
                            ErrorCode.PARAMETER_INVALID.getErrorCode(),
                            name + " must be null"
                    );
                }
                break;
            default:
                // Unknown type, skip validation
                XLogger.debug("Unknown type '" + expectedType + "' for " + name + ", skipping type validation");
        }
    }

    /**
     * Gets a double value from an object.
     *
     * @param obj the object
     * @return the double value, or null if not a valid number
     */
    private Double getDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}

