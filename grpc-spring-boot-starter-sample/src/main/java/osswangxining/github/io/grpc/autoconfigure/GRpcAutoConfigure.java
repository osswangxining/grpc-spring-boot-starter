package osswangxining.github.io.grpc.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.services.HealthStatusManager;
import osswangxining.github.io.grpc.GRpcServerBuilderConfigurer;
import osswangxining.github.io.grpc.GRpcServerRunner;

@Configuration
public class GRpcAutoConfigure {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private GRpcServerProperties grpcServerProperties;

	@Bean
	@ConditionalOnProperty(value = "grpc.enabled", havingValue = "true", matchIfMissing = true)
	public GRpcServerRunner grpcServerRunner(GRpcServerBuilderConfigurer configurer) {
		return new GRpcServerRunner(configurer, ServerBuilder.forPort(grpcServerProperties.getPort()));
	}

	@Bean
	@ConditionalOnExpression("#{environment.getProperty('grpc.inProcessServerName','')!=''}")
	public GRpcServerRunner grpcInprocessServerRunner(GRpcServerBuilderConfigurer configurer) {
		return new GRpcServerRunner(configurer,
				InProcessServerBuilder.forName(grpcServerProperties.getInProcessServerName()));
	}

	@Bean
	public HealthStatusManager healthStatusManager() {
		return new HealthStatusManager();
	}

	@Bean
	@ConditionalOnMissingBean(GRpcServerBuilderConfigurer.class)
	public GRpcServerBuilderConfigurer serverBuilderConfigurer() {
		return new GRpcServerBuilderConfigurer();
	}
}
