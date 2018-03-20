package osswangxining.github.io.grpc;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.util.concurrent.RateLimiter;

import osswangxining.github.io.grpc.annotation.Limiter;
import osswangxining.github.io.ratelimiter.cache.RateLimiterCache;

public class RateLimiterInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean b = true;
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			if (method.isAnnotationPresent(Limiter.class)) {
				Limiter limiter = method.getAnnotation(Limiter.class);
				double value = limiter.value();
				System.out.println("limiter value:" + value);
				String methodAbsKey = getAsbMethodCacheKey(method);
				RateLimiter rateLimiter = null;
				try {
					rateLimiter = RateLimiterCache.get(methodAbsKey);
					System.out.println("rateLimiter/methodAbsKey:" + rateLimiter + "," + methodAbsKey);
				} catch (Exception e) {
					rateLimiter = DefaultRateLimiter.create(value);
					RateLimiterCache.put(methodAbsKey, rateLimiter);
					System.out.println("rateLimiter/methodAbsKey/value:" + rateLimiter + "," + methodAbsKey + "," + value);
				}
				
				if (null == rateLimiter) {
					b = true;
				}
				b = rateLimiter.tryAcquire();
			}
		}
		System.out.println("limit token is acquired:" + b);
		if(!b) {
			throw new RateLimiterException("limit token cannot be acquired.");
		}
		return b;
	}

	public String getAsbMethodCacheKey(Method method) {
		return null == method ? null : method.getDeclaringClass().getName() + "#" + method.getName();
	}
}
