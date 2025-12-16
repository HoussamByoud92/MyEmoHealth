package com.myemohealth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * Intercepts requests, extracts and validates JWT tokens, and sets
 * authentication in SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT from Authorization header
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.validateToken(token)) {
                    setAuthenticationContext(token, request);
                }
            } else {
                // Try from query parameter (for WebSockets)
                String token = request.getParameter("token");
                if (token != null && jwtUtil.validateToken(token)) {
                    setAuthenticationContext(token, request);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        // Extract user information
        String email = jwtUtil.extractEmail(token);
        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        // Create custom principal with user information
        UserPrincipal userPrincipal = new UserPrincipal(userId, email, role);

        // Create authentication object
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Authenticated user: {} (ID: {}) with role: {}", email, userId, role);
    }
}
