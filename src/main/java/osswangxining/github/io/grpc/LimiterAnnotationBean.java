package osswangxining.github.io.grpc;

import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;

import osswangxining.github.io.grpc.annotation.Limiter;
import osswangxining.github.io.ratelimiter.cache.RateLimiterCache;

public class LimiterAnnotationBean implements InitializingBean {
	private String basePackage;

	public LimiterAnnotationBean(String basePackage) {
		this.basePackage = basePackage;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(basePackage), "base package must not be null or empty.");

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(basePackage)).setScanners(new MethodAnnotationsScanner()));

		Set<Method> methods = reflections.getMethodsAnnotatedWith(Limiter.class);

		for (Method method : methods) {

			String methodAbsPath = getAsbMethodCacheKey(method);

			if (!Strings.isNullOrEmpty(methodAbsPath)) {
				Limiter limiter = method.getAnnotation(Limiter.class);
				double value = limiter.value();
				RateLimiter rateLimiter = RateLimiter.create(value);

				RateLimiterCache.put(methodAbsPath, rateLimiter);
			}

		}
	}

	public String getAsbMethodCacheKey(Method method) {
		return null == method ? null : method.getDeclaringClass().getName() + "#" + method.getName();
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}
