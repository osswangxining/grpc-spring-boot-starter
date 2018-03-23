package osswangxining.github.io.grpc.context;

import org.springframework.context.ApplicationEvent;

import io.grpc.Server;

public class GRpcServerInitializedEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4511017146309350099L;

	/**
	 * Create a new ApplicationEvent.
	 * @param source
	 */
	public GRpcServerInitializedEvent(Server source) {
		super(source);
	}

	public Server getServer() {
		return (Server) getSource();
	}
}
