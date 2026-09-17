package com.example.VirtualCardIssuance.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String correlationId = Optional.ofNullable(request.getHeader("correlation-id")).orElse(UUID.randomUUID().toString());
        MDC.put("correlationId",correlationId);
        response.setHeader("correlation-id",correlationId);
        try{
            filterChain.doFilter(request,response);
        }finally {
            MDC.remove("correlationId");
        }
    }
}
