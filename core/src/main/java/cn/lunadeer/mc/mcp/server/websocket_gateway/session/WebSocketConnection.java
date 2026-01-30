package cn.lunadeer.mc.mcp.server.websocket_gateway.session;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for WebSocket connection operations.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public interface WebSocketConnection {
    /**
     * Sends a message to the client.
     *
     * @param message the message to send
     * @return a CompletableFuture that completes when the message is sent
     */
    CompletableFuture<Void> send(String message);

    /**
     * Closes the connection.
     *
     * @param statusCode the WebSocket close status code
     * @param reason     the close reason
     */
    void close(int statusCode, String reason);
}
