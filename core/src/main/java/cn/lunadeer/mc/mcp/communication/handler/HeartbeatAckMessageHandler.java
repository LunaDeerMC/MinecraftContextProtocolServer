package cn.lunadeer.mc.mcp.communication.handler;

import cn.lunadeer.mc.mcp.communication.heartbeat.HeartbeatHandler;
import cn.lunadeer.mc.mcp.communication.message.HeartbeatAck;
import cn.lunadeer.mc.mcp.communication.message.McpMessage;
import cn.lunadeer.mc.mcp.communication.session.GatewaySession;
import cn.lunadeer.mc.mcp.infrastructure.XLogger;

/**
 * Handles heartbeat acknowledgment messages from gateways.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class HeartbeatAckMessageHandler implements MessageHandler {

    private final HeartbeatHandler heartbeatHandler;

    public HeartbeatAckMessageHandler(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
    }

    @Override
    public void handle(GatewaySession session, McpMessage message) {
        if (!(message instanceof HeartbeatAck)) {
            XLogger.warn("HeartbeatAckMessageHandler received non-HeartbeatAck message: " + message.getType());
            return;
        }

        HeartbeatAck ack = (HeartbeatAck) message;
        heartbeatHandler.onHeartbeatAck(session.getId(), ack);
    }

    @Override
    public String getMessageType() {
        return "heartbeat_ack";
    }
}
