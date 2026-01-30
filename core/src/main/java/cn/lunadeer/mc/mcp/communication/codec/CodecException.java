package cn.lunadeer.mc.mcp.communication.codec;

/**
 * Exception thrown when message encoding or decoding fails.
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class CodecException extends RuntimeException {
    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
