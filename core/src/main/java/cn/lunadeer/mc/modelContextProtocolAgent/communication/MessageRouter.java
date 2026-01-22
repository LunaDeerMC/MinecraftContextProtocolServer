package cn.lunadeer.mc.modelContextProtocolAgent.communication;

import cn.lunadeer.mc.modelContextProtocolAgent.communication.handler.MessageHandler;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.message.McpMessage;
import cn.lunadeer.mc.modelContextProtocolAgent.communication.session.GatewaySession;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.I18n;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.XLogger;
import cn.lunadeer.mc.modelContextProtocolAgent.infrastructure.configuration.ConfigurationPart;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Routes incoming MCP messages to appropriate handlers.
 * This is the central message routing component that delegates message processing
 * to dedicated handlers based on message type.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class MessageRouter {

    public static class MessageRouterText extends ConfigurationPart {
        public String routerRoutingMessage = "Routing message type: {0} (id: {1})";
        public String routerUnknownMessageType = "Unknown message type: {0}";
        public String routerMessageError = "Error routing message {0}: {1}";
        public String routerHandlerRegistered = "Message handler registered for type: {0}";
        public String routerHandlerNotFound = "No handler found for message type: {0}";
    }

    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();

    /**
     * Registers a message handler for a specific message type.
     *
     * @param handler the message handler to register
     */
    public void registerHandler(MessageHandler handler) {
        handlers.put(handler.getMessageType(), handler);
        XLogger.debug(I18n.messageRouterText.routerHandlerRegistered, handler.getMessageType());
    }

    /**
     * Routes a message to the appropriate handler.
     *
     * @param session the gateway session that sent the message
     * @param message the message to route
     * @return a CompletableFuture that completes when the message is handled
     */
    public CompletableFuture<Void> route(GatewaySession session, McpMessage message) {
        return CompletableFuture.runAsync(() -> {
            try {
                String type = message.getType();
                XLogger.debug(I18n.messageRouterText.routerRoutingMessage, type, message.getId());

                MessageHandler handler = handlers.get(type);
                if (handler != null) {
                    handler.handle(session, message);
                } else {
                    XLogger.warn(I18n.messageRouterText.routerUnknownMessageType, type);
                }
            } catch (Exception e) {
                XLogger.error(I18n.messageRouterText.routerMessageError, message.getId(), e.getMessage());
            }
        });
    }

    /**
     * Gets the number of registered handlers.
     *
     * @return the number of handlers
     */
    public int getHandlerCount() {
        return handlers.size();
    }

    /**
     * Clears all registered handlers.
     * Useful for testing or shutdown.
     */
    public void clearHandlers() {
        handlers.clear();
    }
}
