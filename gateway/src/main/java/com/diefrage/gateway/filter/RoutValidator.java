package com.diefrage.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RoutValidator {

    public static final List<String> publicEndpoints = List.of(
            "/auth",
            "/api/student",
            "/api/answer",
            "eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> publicEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
