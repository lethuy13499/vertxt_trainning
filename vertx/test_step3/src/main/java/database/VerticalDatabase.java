package database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class VerticalDatabase extends AbstractVerticle {
    public static final String CONFIG_WIKIDB_JDBC_URL = "wikidb.jdbc.url";
    public static final String CONFIG_WIKIDB_JDBC_DRIVER_CLASS = "wikidb.jdbc.driver_class";
    public static final String CONFIG_WIKIDB_JDBC_MAX_POOL_SIZE = "wikidb.jdbc.max_pool_size";
    public static final String CONFIG_WIKIDB_SQL_QUERIES_RESOURCE_FILE = "wikidb.sqlqueries.resource.file";
    public static final String CONFIG_WIKIDB_QUEUE = "wikidb.queue";


    @Override
    public void start(Promise<Void> promise) throws Exception {
        HashMap<VerticalQuery, String> queries = loadQueries();
        JDBCClient client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", config().getString(CONFIG_WIKIDB_JDBC_URL, "jdbc:hsqldb:file:db/wiki"))
                .put("driver_class", config().getString(CONFIG_WIKIDB_JDBC_DRIVER_CLASS, "org.hsqldb.jdbcDriver"))
                .put("max_pool_size", config().getInteger(CONFIG_WIKIDB_JDBC_MAX_POOL_SIZE, 30)));
        PageVerticalService.create(client, queries, ready -> {
            if (ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                        .setAddress(CONFIG_WIKIDB_QUEUE)
                        .register(PageVerticalService.class, ready.result());
                promise.complete();
            } else {
                promise.fail(ready.cause());
            }
        });

    }

    private HashMap<VerticalQuery, String> loadQueries() throws IOException {
        String queriesFiles = config().getString(CONFIG_WIKIDB_SQL_QUERIES_RESOURCE_FILE);
        InputStream queriesStream;
        if (queriesFiles != null) {
            queriesStream = new FileInputStream(queriesFiles);
        } else {
            queriesStream = getClass().getResourceAsStream("/db-queries.properties");
        }
        Properties queriesProps = new Properties();
        queriesProps.load(queriesStream);
        queriesStream.close();
        HashMap<VerticalQuery, String> queries = new HashMap<>();
        queries.put(VerticalQuery.CREATE_PAGES_TABLE, queriesProps.getProperty("create-pages-table"));
        queries.put(VerticalQuery.ALL_PAGES, queriesProps.getProperty("all-pages"));
        queries.put(VerticalQuery.GET_PAGE, queriesProps.getProperty("get-page"));
        queries.put(VerticalQuery.CREATE_PAGE, queriesProps.getProperty("create-page"));
        queries.put(VerticalQuery.SAVE_PAGE, queriesProps.getProperty("save-page"));
        queries.put(VerticalQuery.DELETE_PAGE, queriesProps.getProperty("delete-page"));
        return queries;
    }

}
