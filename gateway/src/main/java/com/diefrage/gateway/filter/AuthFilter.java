package com.diefrage.gateway.filter;

import com.diefrage.exceptions.TypicalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Autowired
    private RoutValidator validator;

    @Autowired
    private RestTemplate restTemplate;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            System.out.println(exchange.getRequest().getPath());
            if (validator.isSecured.test(exchange.getRequest())){
                // header contains or not token
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    TypicalServerException.USER_NOT_FOUND.throwException();
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")){
                    authHeader =  authHeader.substring(7);
                }

                // Rest call
                System.out.println(authHeader);
                restTemplate.getForObject("http://localhost:8010/auth/validate?token=" + authHeader, Boolean.class);

            }
            return chain.filter(exchange);
        }));
    }

    public static class Config { }
}
