package cn.lunadeer.mc.modelContextProtocolAgent.communication.handler;

import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpMessage;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.GatewaySession;

/**
 * Interface for handling MCP messages.
 * Each message type should have a dedicated handler implementation.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public interface MessageHandler {

    /**
     * Handles a message from a gateway session.
     *
     * @param session the gateway session that sent the message
     * @param message the message to handle
     */
    void handle(GatewaySession session, McpMessage message);

    /**
     * Returns the message type this handler can handle.
     *
     * @return the message type string
     */
    String getMessageType();
}
