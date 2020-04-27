package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void startVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Start a web server on localhost responding to path /service on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_server(Vertx vertx, VertxTestContext testContext) {
    WebClient.create(vertx)
        .get(8080, "::1", "/service")
        .send(response -> testContext.verify(() -> {
          assertEquals(200, response.result().statusCode());
          JsonArray body = response.result().bodyAsJsonArray();
          assertNotNull( body.size());
          testContext.completeNow();
        }));
  }

    @Test
    @DisplayName("Insert new service test")
    @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
    void insertService(Vertx vertx, VertxTestContext testContext) {
        JsonObject payload = new JsonObject().put("url", "http://www.google.com").put("name", "google");
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJsonObject(payload, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    String body = response.result().bodyAsString();
                    assertEquals("OK", body);

                    WebClient.create(vertx)
                            .get(8080, "::1", "/service")
                            .send(r -> testContext.verify(() -> {
                                assertEquals(200, r.result().statusCode());
                                JsonArray b = r.result().bodyAsJsonArray();
                                for(int i =0;i< b.size();i++){
                                    JsonObject service = b.getJsonObject(i);
                                    if(service.getString("url").equalsIgnoreCase("http://www.google.com")){
                                        assertEquals("http://www.google.com", service.getString("url"));
                                        assertEquals("google", service.getString("name"));
                                        assertEquals("UNKNOWN", service.getString("status"));
                                        assertNotNull(service.getString("createdAt"));
                                    }
                                }

                                testContext.completeNow();
                            }));
                }));
    }



    @Test
    @DisplayName("insert a new service with invalid url")
    @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
    void insertServiceWithInvalidUrl(Vertx vertx, VertxTestContext testContext) {
        JsonObject payload = new JsonObject().put("url", "www.google.com");
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJsonObject(payload, response -> testContext.verify(() -> {
                    assertEquals(400, response.result().statusCode());
                    String body = response.result().bodyAsString();
                    assertEquals("Invalid url: www.google.com", body);
                    testContext.completeNow();

                }));
    }

    @Test
    @DisplayName("delete an existing service")
    @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
    void deleteService(Vertx vertx, VertxTestContext testContext) throws Exception {
        JsonObject payload = new JsonObject().put("url", "http://www.google.com");
        WebClient.create(vertx)
                .delete(8080, "::1", "/delete")
                .sendJsonObject(payload,response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    String body = response.result().bodyAsString();
                    assertEquals("OK", body);

                    WebClient.create(vertx)
                            .get(8080, "::1", "/service")
                            .send(r -> testContext.verify(() -> {
                                assertEquals(200, r.result().statusCode());
                                JsonArray b = r.result().bodyAsJsonArray();
                                for(int i =0;i< b.size();i++){
                                    JsonObject service = b.getJsonObject(i);
                                    if(!service.getString("url").equalsIgnoreCase("http://www.google.com")){
                                        assertNotEquals("http://www.google.com", service.getString("url"));

                                    }
                                }
                                testContext.completeNow();
                            }));
                }));
    }

}
