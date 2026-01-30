package cn.lunadeer.mc.mcp.server.websocket_gateway.handler;

import cn.lunadeer.mc.mcp.server.websocket_gateway.codec.MessageCodec;
import cn.lunadeer.mc.mcp.server.websocket_gateway.message.McpMessage;
import cn.lunadeer.mc.mcp.server.websocket_gateway.message.McpRequest;
import cn.lunadeer.mc.mcp.server.websocket_gateway.message.McpResponse;
import cn.lunadeer.mc.mcp.server.websocket_gateway.session.GatewaySession;
import cn.lunadeer.mc.mcp.core.execution.CallerInfo;
import cn.lunadeer.mc.mcp.core.execution.ExecutionEngine;
import cn.lunadeer.mc.mcp.infrastructure.XLogger;
import cn.lunadeer.mc.mcp.sdk.model.ErrorCode;

import java.util.Collections;

/**
 * Handles capability request messages from gateways.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class RequestMessageHandler implements MessageHandler {

    private final MessageCodec messageCodec;
    private final ExecutionEngine executionEngine;

    public RequestMessageHandler(MessageCodec messageCodec, ExecutionEngine executionEngine) {
        this.messageCodec = messageCodec;
        this.executionEngine = executionEngine;
    }

    @Override
    public void handle(GatewaySession session, McpMessage message) {
        if (!(message instanceof McpRequest)) {
            XLogger.warn("RequestMessageHandler received non-McpRequest message: " + message.getType());
            return;
        }

        McpRequest request = (McpRequest) message;

        if (!session.isAuthenticated()) {
            XLogger.warn("Unauthenticated gateway " + session.getGatewayId() + " attempted request");
            return;
        }

        // Create CallerInfo from session
        CallerInfo caller = new CallerInfo(
                session.getGatewayId(),
                session.getGatewayId(),
                session.getPermissions(),
                Collections.emptySet()
        );

        // Execute the capability using the execution engine
        executionEngine.execute(request, caller).thenAccept(response -> {
            String jsonResponse = messageCodec.encode(response);
            session.send(jsonResponse);
        }).exceptionally(ex -> {
            // Handle execution errors
            McpResponse errorResponse = McpResponse.error(
                    request.getId(),
                    ErrorCode.INTERNAL_ERROR,
                    "Failed to execute capability: " + ex.getMessage()
            ).build();
            String jsonResponse = messageCodec.encode(errorResponse);
            session.send(jsonResponse);
            return null;
        });
    }

    @Override
    public String getMessageType() {
        return "request";
    }
}
