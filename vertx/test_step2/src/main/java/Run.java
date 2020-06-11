import io.vertx.core.Vertx;

public class Run {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVertical());
    }
}
