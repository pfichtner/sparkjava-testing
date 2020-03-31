package com.fortitudetec.testing.junit5.spark;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fortitudetec.testing.junit5.spark.JavaSparkServiceExtension.JavaSparkServiceStarter;

@ExtendWith(JavaSparkServiceExtension.class)
class SparkServerRuleTest {

    private Client client = ClientBuilder.newClient();

	@BeforeEach
	void setUp(JavaSparkServiceStarter s) {
		s.setup(http -> {
			http.get("/ping", (request, response) -> "pong");
			http.get("/health", (request, response) -> "healthy");
		});
	}

    @AfterEach
    void tearDown() {
        Optional.ofNullable(client).ifPresent(Client::close);
    }

    @Test
    void testSparkServerRule_PingRequest() {
        Response response = client.target(URI.create("http://localhost:4567/ping"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("pong");
    }

    @Test
    void testSparkServerRule_HealthRequest() {
        Response response = client.target(URI.create("http://localhost:4567/health"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("healthy");
    }

}