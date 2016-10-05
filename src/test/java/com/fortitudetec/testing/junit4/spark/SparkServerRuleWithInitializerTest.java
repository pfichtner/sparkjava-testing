package com.fortitudetec.testing.junit4.spark;

import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import spark.Spark;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static spark.Spark.get;

public class SparkServerRuleWithInitializerTest {

    private Client client;
    private HostnameVerifier defaultHostnameVerifier;

    @ClassRule
    public static final SparkServerRule SPARK_SERVER = new SparkServerRule(
            () -> {
                Spark.ipAddress("127.0.0.1");
                Spark.port(9876);
                URL resource = Resources.getResource("sample-keystore.jks");
                String file = resource.getFile();
                Spark.secure(file, "password", null, null);
            },
            () -> {
                get("/ping", (request, response) -> "pong");
                get("/health", (request, response) -> "healthy");
            });

    @Before
    public void setUp() {
        defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

        // Create and install all-trusting host name verifier (so both localhost and 127.0.0.1 will work)
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    @After
    public void tearDown() {
        Optional.ofNullable(client).ifPresent(Client::close);

        HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
    }

    @Test
    public void testSparkServerRule_PingRequest() {
        client = ClientBuilder.newBuilder()
                .sslContext(createSSLContext())
                .build();

        Response response = client.target(URI.create("https://127.0.0.1:9876/ping"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("pong");
    }

    @Test
    public void testSparkServerRule_HealthRequest() {
        client = ClientBuilder.newBuilder()
                .sslContext(createSSLContext())
                .build();

        Response response = client.target(URI.create("https://localhost:9876/health"))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("healthy");
    }

    private SSLContext createSSLContext() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new NoOpX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
