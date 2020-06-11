import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVertical extends AbstractVerticle {
    @Override
    public void start(Promise<Void> promise) throws Exception {
        Promise<String> dbVertical = Promise.promise();
        vertx.deployVerticle(new Database(), dbVertical);
        dbVertical.future().compose(id -> {
            Promise<String> httpVertical = Promise.promise();
            vertx.deployVerticle("HttpServerVerticle", new DeploymentOptions().setInstances(2),
                    httpVertical);
            return httpVertical.future();

        }).setHandler(ar -> {
            if (ar.succeeded()) {
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });
    }
}
