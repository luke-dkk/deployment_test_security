package dk.ek;

import dk.ek.rest.ApplicationConfig;
import dk.ek.rest.Routes;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main {
    public static void main(String[] args) {
        Routes routes = new Routes();
        new ApplicationConfig()
                .security()
//                .route(securityRoutes.getRouteResource("auth"))
//                .route(securityRoutes.getRouteResource("protected"))
//                .route(restRoutes.getRouteResource("open/person"))
                .route(routes.getRouteResource("msg"))
                .route(routes.getRouteResource("auth"))
//                .route(() -> {
//                    path("/index", () -> {
//                        get("/", ctx -> ctx.render("index.html"));
//                    });
//                })
                .cors()
                .exceptions()
                .apiExceptions()
                .start(7070);
    }
}