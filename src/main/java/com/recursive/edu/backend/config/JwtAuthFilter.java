/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recursive.edu.backend.model.common.ApiResult;
import com.recursive.edu.backend.model.common.Error;
import com.recursive.edu.backend.model.exception.MissingAuthTokenException;
import com.recursive.edu.backend.model.user.AuthenticatedUser;
import com.recursive.edu.backend.service.JwtService;
import com.recursive.edu.backend.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceImpl userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtService jwtService, UserServiceImpl userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MissingAuthTokenException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            com.recursive.edu.backend.model.user.UserDetails user = jwtService.extractUserDetails(token);

            if (user != null && user.getEmail() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

                if (jwtService.isTokenValid(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            new AuthenticatedUser(user.getId(), user.getUuid(), user.getEmail(), userDetails.getAuthorities()),
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // ✅ Continue the chain only if no exception
            filterChain.doFilter(request, response);

        } catch (MissingAuthTokenException | AccessDeniedException ex) {
            writeErrorResponse(response, ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException ex) {
            writeErrorResponse(response, "JWT token expired", HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException ex) {
            writeErrorResponse(response, "Invalid credentials", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            // ✅ Catch *any other* unhandled exception
            writeErrorResponse(response, "Internal authentication error: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/signup")
                || path.startsWith("/auth/refresh")
                || path.startsWith("/error");
    }

    private void writeErrorResponse(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        if (response.isCommitted()) return;

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResult result = ApiResult.newInstance()
                .withErrors(List.of(Error.createInstance(message)))
                .withStatus(String.valueOf(status.value()))
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
