package database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.stream.Collectors;

public class PageVerticalServiceImpl implements PageVerticalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageVerticalServiceImpl.class);

    private final HashMap<VerticalQuery, String> sqlQueries;
    private final JDBCClient client;

    PageVerticalServiceImpl(JDBCClient client, HashMap<VerticalQuery, String> sqlQueries,
                            Handler<AsyncResult<PageVerticalService>> readyHandler) {
        this.client = client;
        this.sqlQueries = sqlQueries;

        client.getConnection(ar -> {
            if (ar.failed()) {
                LOGGER.error("Could not open a database connection", ar.cause());
                readyHandler.handle(Future.failedFuture(ar.cause()));
            } else {
                SQLConnection connection = ar.result();
                connection.execute(sqlQueries.get(VerticalQuery.CREATE_PAGES_TABLE), create -> {
                    connection.close();
                    if (create.failed()) {
                        LOGGER.error("Database preparation error", create.cause());
                        readyHandler.handle(Future.failedFuture(create.cause()));
                    } else {
                        readyHandler.handle(Future.succeededFuture(this));
                    }
                });
            }
        });
    }

    @Override
    public PageVerticalService fetchAllPages(Handler<AsyncResult<JsonArray>> resultHandler) {
        client.query(sqlQueries.get(VerticalQuery.ALL_PAGES), res -> {
            if (res.succeeded()) {
                JsonArray pages = new JsonArray(res.result()
                        .getResults()
                        .stream()
                        .map(json -> json.getString(0))
                        .sorted()
                        .collect(Collectors.toList()));
                resultHandler.handle(Future.succeededFuture(pages));
            } else {
                LOGGER.error("Database query error", res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }

    @Override
    public PageVerticalService fetchPage(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
        client.queryWithParams(sqlQueries.get(VerticalQuery.GET_PAGE), new JsonArray().add(name), fetch -> {
            if (fetch.succeeded()) {
                JsonObject response = new JsonObject();
                ResultSet resultSet = fetch.result();
                if (resultSet.getNumRows() == 0) {
                    response.put("found", false);
                } else {
                    response.put("found", true);
                    JsonArray row = resultSet.getResults().get(0);
                    response.put("id", row.getInteger(0));
                    response.put("rawContent", row.getString(1));
                }
                resultHandler.handle(Future.succeededFuture(response));
            } else {
                LOGGER.error("Database query error", fetch.cause());
                resultHandler.handle(Future.failedFuture(fetch.cause()));
            }
        });
        return this;
    }

    @Override
    public PageVerticalService createPage(String title, String markdown, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray data = new JsonArray().add(title).add(markdown);
        client.updateWithParams(sqlQueries.get(VerticalQuery.CREATE_PAGE), data, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Database query error", res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }

    @Override
    public PageVerticalService savePage(int id, String markdown, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray data = new JsonArray().add(markdown).add(id);
        client.updateWithParams(sqlQueries.get(VerticalQuery.SAVE_PAGE), data, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Database query error", res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }

    @Override
    public PageVerticalService deletePage(int id, Handler<AsyncResult<Void>> resultHandler) {
        JsonArray data = new JsonArray().add(id);
        client.updateWithParams(sqlQueries.get(VerticalQuery.DELETE_PAGE), data, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.error("Database query error", res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }
}
