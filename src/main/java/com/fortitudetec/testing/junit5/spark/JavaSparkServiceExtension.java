package com.fortitudetec.testing.junit5.spark;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import spark.Service;

public class JavaSparkServiceExtension implements ParameterResolver {

	public static class JavaSparkServiceStarter implements CloseableResource {

		private Service service;

		public JavaSparkServiceStarter setup(Consumer<Service> consumer) {
			service = Service.ignite();
			consumer.accept(service);
			service.awaitInitialization();
			return this;
		}

		@Override
		public void close() {
			Optional.ofNullable(service).ifPresent(Service::stop);
		}

	}

	private static final Namespace NAMESPACE = create(JavaSparkServiceExtension.class);

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return appliesTo(parameterContext.getParameter().getType());
	}

	private boolean appliesTo(Class<?> type) {
		return type == JavaSparkServiceStarter.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return extensionContext.getStore(NAMESPACE) //
				.getOrComputeIfAbsent(parameterContext, key -> new JavaSparkServiceStarter(),
						JavaSparkServiceStarter.class);
	}

}
