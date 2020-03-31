package com.fortitudetec.testing.junit5.spark;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import spark.ExceptionHandler;
import spark.Filter;
import spark.FilterImpl;
import spark.HaltException;
import spark.ResponseTransformer;
import spark.Route;
import spark.RouteGroup;
import spark.RouteImpl;
import spark.Service;
import spark.Spark;
import spark.TemplateEngine;
import spark.TemplateViewRoute;
import spark.route.HttpMethod;

public class JavaSparkRunnerExtension implements ParameterResolver {

	public static class SparkStarter implements CloseableResource {

		// artificial interface that present the static Spark instance or a Service
		// instance
		public static interface IService {

			Service ipAddress(String ipAddress);

			Service port(int port);

			int port();

			Service secure(String keystoreFile, String keystorePassword, String truststoreFile,
					String truststorePassword);

			Service secure(String keystoreFile, String keystorePassword, String certAlias, String truststoreFile,
					String truststorePassword);

			Service secure(String keystoreFile, String keystorePassword, String truststoreFile,
					String truststorePassword, boolean needsClientCert);

			Service secure(String keystoreFile, String keystorePassword, String certAlias, String truststoreFile,
					String truststorePassword, boolean needsClientCert);

			Service threadPool(int maxThreads);

			Service threadPool(int maxThreads, int minThreads, int idleTimeoutMillis);

			Service staticFileLocation(String folder);

			Service externalStaticFileLocation(String externalFolder);

			void webSocket(String path, Class<?> handlerClass);

			void webSocket(String path, Object handler);

			Service webSocketIdleTimeoutMillis(int timeoutMillis);

			void notFound(String page);

			void internalServerError(String page);

			void notFound(Route route);

			void internalServerError(Route route);

			void awaitInitialization();

			void stop();

			void awaitStop();

			void path(String path, RouteGroup routeGroup);

			String getPaths();

			void addRoute(HttpMethod httpMethod, RouteImpl route);

			void addFilter(HttpMethod httpMethod, FilterImpl filter);

			void init();

			int activeThreadCount();

			HaltException halt();

			HaltException halt(int status);

			HaltException halt(String body);

			HaltException halt(int status, String body);

			void initExceptionHandler(Consumer<Exception> initExceptionHandler);

			void get(String path, Route route);

			void post(String path, Route route);

			void put(String path, Route route);

			void patch(String path, Route route);

			void delete(String path, Route route);

			void head(String path, Route route);

			void trace(String path, Route route);

			void connect(String path, Route route);

			void options(String path, Route route);

			void before(String path, Filter filter);

			void after(String path, Filter filter);

			void get(String path, String acceptType, Route route);

			void post(String path, String acceptType, Route route);

			void put(String path, String acceptType, Route route);

			void patch(String path, String acceptType, Route route);

			void delete(String path, String acceptType, Route route);

			void head(String path, String acceptType, Route route);

			void trace(String path, String acceptType, Route route);

			void connect(String path, String acceptType, Route route);

			void options(String path, String acceptType, Route route);

			void before(Filter filter);

			void after(Filter filter);

			void before(String path, String acceptType, Filter filter);

			void after(String path, String acceptType, Filter filter);

			void afterAfter(Filter filter);

			void afterAfter(String path, Filter filter);

			void get(String path, TemplateViewRoute route, TemplateEngine engine);

			void get(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void post(String path, TemplateViewRoute route, TemplateEngine engine);

			void post(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void put(String path, TemplateViewRoute route, TemplateEngine engine);

			void put(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void delete(String path, TemplateViewRoute route, TemplateEngine engine);

			void delete(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void patch(String path, TemplateViewRoute route, TemplateEngine engine);

			void patch(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void head(String path, TemplateViewRoute route, TemplateEngine engine);

			void head(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void trace(String path, TemplateViewRoute route, TemplateEngine engine);

			void trace(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void connect(String path, TemplateViewRoute route, TemplateEngine engine);

			void connect(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void options(String path, TemplateViewRoute route, TemplateEngine engine);

			void options(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine);

			void get(String path, Route route, ResponseTransformer transformer);

			void get(String path, String acceptType, Route route, ResponseTransformer transformer);

			void post(String path, Route route, ResponseTransformer transformer);

			void post(String path, String acceptType, Route route, ResponseTransformer transformer);

			void put(String path, Route route, ResponseTransformer transformer);

			void put(String path, String acceptType, Route route, ResponseTransformer transformer);

			void delete(String path, Route route, ResponseTransformer transformer);

			void delete(String path, String acceptType, Route route, ResponseTransformer transformer);

			void head(String path, Route route, ResponseTransformer transformer);

			void head(String path, String acceptType, Route route, ResponseTransformer transformer);

			void connect(String path, Route route, ResponseTransformer transformer);

			void connect(String path, String acceptType, Route route, ResponseTransformer transformer);

			void trace(String path, Route route, ResponseTransformer transformer);

			void trace(String path, String acceptType, Route route, ResponseTransformer transformer);

			void options(String path, Route route, ResponseTransformer transformer);

			void options(String path, String acceptType, Route route, ResponseTransformer transformer);

			void patch(String path, Route route, ResponseTransformer transformer);

			void patch(String path, String acceptType, Route route, ResponseTransformer transformer);

			RouteImpl createRouteImpl(String path, String acceptType, Route route);

			RouteImpl createRouteImpl(String path, Route route);

			void defaultResponseTransformer(ResponseTransformer transformer);

		}

		private IService service;

		public SparkStarter runService(Consumer<IService> consumer) {
			return runService(Service.ignite(), consumer);
		}

		private SparkStarter runService(Service service, Consumer<IService> consumer) {
			return runGeneral(consumer, Service.class, service);
		}

		public SparkStarter runSpark(Runnable runnable) {
			return runGeneral(ignore -> runnable.run(), Spark.class, null);
		}

		private SparkStarter runGeneral(Consumer<IService> consumer, Class<?> clazz, Service instance) {
			service = IService.class
					.cast(newProxyInstance(getClass().getClassLoader(), new Class<?>[] { IService.class },
							(proxy, method, args) -> execute(instance, clazz, method, args)));
			consumer.accept(service);
			service.awaitInitialization();
			return this;
		}

		private Object execute(Object instance, Class<? extends Object> clazz, Method method, Object[] args)
				throws IllegalAccessException, NoSuchMethodException, Throwable {
			try {
				return clazz.getMethod(method.getName(), method.getParameterTypes()).invoke(instance, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		@Override
		public void close() {
			Optional.ofNullable(service).ifPresent(IService::stop);
		}

	}

	private static final Namespace NAMESPACE = create(JavaSparkRunnerExtension.class);

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return appliesTo(parameterContext.getParameter().getType());
	}

	private boolean appliesTo(Class<?> type) {
		return type == SparkStarter.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return extensionContext.getStore(NAMESPACE) //
				.getOrComputeIfAbsent(parameterContext, key -> new SparkStarter(), SparkStarter.class);
	}

}
