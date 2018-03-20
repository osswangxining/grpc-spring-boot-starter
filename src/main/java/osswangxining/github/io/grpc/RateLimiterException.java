package osswangxining.github.io.grpc;

public class RateLimiterException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3119416813619871276L;

	public RateLimiterException() {
        super();
    }

    public RateLimiterException(String message) {
        super(message);
    }

    public RateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimiterException(Throwable cause) {
        super(cause);
    }

    protected RateLimiterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
