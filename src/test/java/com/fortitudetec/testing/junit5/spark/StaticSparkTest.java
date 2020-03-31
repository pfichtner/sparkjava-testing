package com.fortitudetec.testing.junit5.spark;

import static org.assertj.core.api.Assertions.assertThat;
import static spark.Spark.get;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fortitudetec.testing.junit5.spark.JavaSparkSparkExtension.JavaSparkSparkStarter;

@ExtendWith(JavaSparkSparkExtension.class)
class StaticSparkTest {

	private Client client;

	@BeforeAll
	static void setUp(JavaSparkSparkStarter s) {
		s.setup(() -> {
			get("/ping", (request, response) -> "pong");
			get("/health", (request, response) -> "healthy");
		});
	}

	@AfterEach
	void tearDown() {
		Optional.ofNullable(client).ifPresent(Client::close);
	}

	@Test
	void testSparkServerRule_PingRequest() {
		client = ClientBuilder.newBuilder().build();
		Response response = client.target(URI.create("http://localhost:4567/ping")).request().get();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.readEntity(String.class)).isEqualTo("pong");
	}

	@Test
	void testSparkServerRule_HealthRequest() {
		client = ClientBuilder.newBuilder().build();
		Response response = client.target(URI.create("http://localhost:4567/health")).request().get();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.readEntity(String.class)).isEqualTo("healthy");
	}

}