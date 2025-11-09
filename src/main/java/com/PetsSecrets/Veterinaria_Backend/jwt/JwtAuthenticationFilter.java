package com.PetsSecrets.Veterinaria_Backend.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        System.out.println("=== JWT FILTER - URI: " + requestURI + " ===");
        
        try {
            String jwt = parseJwt(request);
            System.out.println("JWT extraído: " + (jwt != null ? jwt.substring(0, Math.min(20, jwt.length())) + "..." : "null"));
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                System.out.println("JWT válido");
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                System.out.println("Email del JWT: " + email);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("UserDetails cargado: " + userDetails.getUsername());
                System.out.println("Authorities: " + userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Autenticación establecida correctamente");
            } else {
                System.out.println("JWT nulo o inválido");
            }
        } catch (Exception e) {
            System.err.println("Error en JWT filter: " + e.getMessage());
            e.printStackTrace();
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
