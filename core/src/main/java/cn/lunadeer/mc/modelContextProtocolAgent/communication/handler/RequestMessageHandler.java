package cn.lunadeer.mc.modelContextProtocolAgent.communication.handler;

import cn.lunadeer.mc.modelContextProtocolAgent.communication.codec.MessageCodec;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpMessage;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpRequest;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpResponse;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.GatewaySession;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.XLogger;

import java.util.Map;

/**
 * Handles capability request messages from gateways.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class RequestMessageHandler implements MessageHandler {

    private final MessageCodec messageCodec;

    public RequestMessageHandler(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
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

        // TODO: Implement request handling with execution engine
        // For now, send a placeholder response
        McpResponse response = McpResponse.success(
                request.getId(),
                Map.of("message", "Request received (not yet implemented)")
        ).build();

        String jsonResponse = messageCodec.encode(response);
        session.send(jsonResponse);
    }

    @Override
    public String getMessageType() {
        return "request";
    }
}
