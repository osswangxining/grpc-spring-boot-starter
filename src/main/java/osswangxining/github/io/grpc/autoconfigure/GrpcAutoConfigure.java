package osswangxining.github.io.grpc.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import osswangxining.github.io.grpc.LimiterAnnotationBean;

@Configuration
public class GrpcAutoConfigure {
	@Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rate.limiter",value = "enabled",havingValue = "true")
	public LimiterAnnotationBean limiterAnnotationBean() {
		return new LimiterAnnotationBean("osswangxining.github.io.ratelimiter.autoconfigure");
	}
}
