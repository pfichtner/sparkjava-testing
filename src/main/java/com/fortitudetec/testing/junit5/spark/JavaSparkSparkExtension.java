package com.fortitudetec.testing.junit5.spark;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class JavaSparkSparkExtension implements ParameterResolver {

	public static class JavaSparkSparkStarter implements CloseableResource {

		public JavaSparkSparkStarter setup(Runnable runnable) {
			runnable.run();
			awaitInitialization();
			return this;
		}

		@Override
		public void close() {
			stop();
		}

	}

	private static final Namespace NAMESPACE = create(JavaSparkSparkExtension.class);

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return appliesTo(parameterContext.getParameter().getType());
	}

	private boolean appliesTo(Class<?> type) {
		return type == JavaSparkSparkStarter.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return extensionContext.getStore(NAMESPACE) //
				.getOrComputeIfAbsent(parameterContext, key -> new JavaSparkSparkStarter(),
						JavaSparkSparkStarter.class);
	}

}
