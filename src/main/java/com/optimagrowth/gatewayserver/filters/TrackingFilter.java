package com.optimagrowth.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {
    private final static Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    /* this part of code will be working every time when request run through the filter */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders(); /* headers are getting from ServerWebExchange */
        System.out.println("sout: "+requestHeaders);
        if (isCorrelationIdPresent(requestHeaders)) {
            logger.debug("tmx-correlation-id found in tracking filter: {}",
                    FilterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationId = FilterUtils.generateCorrelationId();
            exchange = FilterUtils.setCorrelationId(exchange, correlationId);
            logger.debug("tmx-correlation-id NOT found and generated in tracking filter: {}",
                    correlationId);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders headers) {
        if (FilterUtils.getCorrelationId(headers) != null) {
            return true;
        } else {
            return false;
        }
    }
}
