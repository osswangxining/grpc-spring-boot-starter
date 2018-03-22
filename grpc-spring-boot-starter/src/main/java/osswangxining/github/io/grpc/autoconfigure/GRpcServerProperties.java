package osswangxining.github.io.grpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties(prefix = "grpc")
@Data
public class GRpcServerProperties {
	/**
	 * gRPC server port
	 *
	 */
	private int port = 6565;
	/**
	 * Enables the embedded grpc server.
	 */
	private boolean enabled = true;
	/**
     * In process server name.
     * If  the value is not empty, the embedded in-process server will be created and started.
     *
     */
    private String inProcessServerName;
}
