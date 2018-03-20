package osswangxining.github.io.grpc.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRpcAutoConfigure {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private GRpcServerProperties grpcServerProperties;
}
