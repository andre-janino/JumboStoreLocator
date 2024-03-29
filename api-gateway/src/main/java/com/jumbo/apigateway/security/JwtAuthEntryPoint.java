package com.jumbo.apigateway.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for returning a custom message when authentication fails.
 * 
 * @author André Janino
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
     
        String json = String.format("{\"message\": \"%s\"}", e.getMessage());
        log.info("Sending UNAUTHORIZED access response: " + json);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);       
    }
}