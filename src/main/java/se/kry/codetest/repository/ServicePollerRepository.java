package se.kry.codetest.repository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import se.kry.codetest.DBConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServicePollerRepository {
    private final Logger LOGGER = LoggerFactory.getLogger(ServicePollerRepository.class);

    private final DBConnector connector;

    private final HashMap<String, JsonObject> services = new HashMap<>();
    private static String URL = "url";
    private static String STATUS = "status";
    private static String NAME = "name";
    private static String UNKNOWN = "UNKNOWN";
    private static String QUERY_GET_ALL_SERVICES = "SELECT * FROM service;";
    private static String QUERY_DELETE_SERVICE = "DELETE FROM service WHERE url=?";
    private static String QUERY_INSERT_UPDATE_SERVICE =  "INSERT OR REPLACE INTO service (url, name, createdAt)" +
            " values (?,?,DATE('now','localtime')" +
            ")";

    public ServicePollerRepository(DBConnector connector) {
        this.connector = connector;
    }

    public Future<Boolean> setup() {
        Future<Boolean> setupFuture = Future.future();
        connector.query(QUERY_GET_ALL_SERVICES).setHandler(result -> {
            if (result.succeeded()) {
                result.result().getRows().forEach(row -> services.put(row.getString(URL), row.put(STATUS, UNKNOWN)));
                LOGGER.info("Services already present :" + services.keySet());
                setupFuture.complete(true);
            } else {
                LOGGER.error("Error while connecting database", result.cause());
                setupFuture.fail(result.cause());
            }
        });
        return setupFuture;
    }
    public Future<ResultSet> insertOrUpdate(JsonObject service) {
        services.put(service.getString(URL), service);
        return connector.query(QUERY_INSERT_UPDATE_SERVICE,
                new JsonArray()
                        .add(service.getString(URL))
                        .add(service.getString(NAME))

        );
    }


    public Future<ResultSet> delete(String service) {
        services.remove(service);
        return connector.query(QUERY_DELETE_SERVICE, new JsonArray().add(service));
    }
    public List<JsonObject> getAllServices() {
        return new ArrayList<>(services.values());
    }

}
