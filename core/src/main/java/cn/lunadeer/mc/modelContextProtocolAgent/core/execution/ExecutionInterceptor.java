package cn.lunadeer.mc.modelContextProtocolAgent.core.execution;

import cn.lunadeer.mc.modelContextProtocolAgentSDK.exception.McpException;

/**
 * Interceptor for capability execution.
 * <p>
 * Interceptors can modify execution context, perform pre/post checks,
 * and control whether execution should proceed.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public interface ExecutionInterceptor {

    /**
     * Called before capability execution.
     * <p>
     * Returns true to continue execution, false to skip execution.
     * </p>
     *
     * @param context the execution context
     * @return true to continue, false to skip
     * @throws McpException if validation fails
     */
    boolean preHandle(ExecutionContext context) throws McpException;

    /**
     * Called after successful capability execution.
     *
     * @param context the execution context
     * @param result  the execution result
     */
    void postHandle(ExecutionContext context, Object result);

    /**
     * Called when an exception occurs during execution.
     *
     * @param context the execution context
     * @param ex      the exception
     */
    void onError(ExecutionContext context, Throwable ex);

    /**
     * Gets the execution order (lower values execute first).
     *
     * @return the order value
     */
    int getOrder();
}
