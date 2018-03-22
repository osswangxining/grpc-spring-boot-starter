package osswangxining.github.io.grpc.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

import io.grpc.ServerInterceptor;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Service
public @interface GRpcService {
	Class<? extends ServerInterceptor>[] interceptors() default {};

	boolean applyGlobalInterceptors() default true;
}
