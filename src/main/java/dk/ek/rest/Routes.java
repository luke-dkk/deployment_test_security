package dk.ek.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.ek.exceptions.ApiException;
import dk.ek.persistence.Department;
import dk.ek.persistence.DepartmentDAO;
import dk.ek.persistence.Employee;
import dk.ek.persistence.EmployeeDAO;
import dk.ek.persistence.HibernateConfig;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityNotFoundException;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;
import com.fasterxml.jackson.core.*;
public class Routes {

    private final EmployeeDAO employeeDAO = new EmployeeDAO(HibernateConfig.getEntityManagerFactory());
    private final DepartmentDAO departmentDAO = new DepartmentDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper objectMapper = new ObjectMapper();
    private final ISecurityController securityController = new SecurityController();

    public EndpointGroup getRouteResource(String resourceName) {
        return switch (resourceName.toLowerCase()) {
            case "msg" -> () -> path("msg", () -> {
            ObjectNode on = objectMapper.createObjectNode();
            on.put("msg", "Hello World");
                get("hello", ctx -> ctx.json(on));
                post("echo", ctx -> ctx.result(ctx.body()));
            });
//
            case "auth" -> () -> path("auth", () -> {
                ObjectNode on = objectMapper.createObjectNode();
                on.put("msg", "Hello World from protected user route");
                post("register", securityController::register );
                post("login", securityController::login);
                get("protected", ctx->ctx.json(on).status(200), Role.USER, Role.ADMIN);
            });
            default -> throw new IllegalArgumentException("Unknown resource name: " + resourceName);
        };
    }
    public enum Role implements RouteRole {
        ANYONE, USER, ADMIN
    }

}


