package database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.HashMap;

@ProxyGen
@VertxGen
public interface PageVerticalService {

    @GenIgnore

    static PageVerticalService createProxy(Vertx vertx, String adress) {
        return new PageVerticalServiceVertxEBProxy(vertx, adress);
    }




    @Fluent
    PageVerticalService fetchAllPages(Handler<AsyncResult<JsonArray>> resultHandler);

    @Fluent
    PageVerticalService fetchPage(String name, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    PageVerticalService createPage(String title, String markdown, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    PageVerticalService savePage(int id, String markdown, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    PageVerticalService deletePage(int id, Handler<AsyncResult<Void>> resultHandler);


    @GenIgnore
    static PageVerticalService create(JDBCClient client, HashMap<VerticalQuery, String> sqlQueries,
                                      Handler<AsyncResult<PageVerticalService>> readyHandler){
        return new PageVerticalServiceImpl(client, sqlQueries, readyHandler);
    }

    }


