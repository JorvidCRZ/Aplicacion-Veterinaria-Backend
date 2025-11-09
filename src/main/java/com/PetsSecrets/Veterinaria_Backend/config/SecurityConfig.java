package com.PetsSecrets.Veterinaria_Backend.config;

import com.PetsSecrets.Veterinaria_Backend.jwt.JwtAuthenticationFilter;
import com.PetsSecrets.Veterinaria_Backend.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${app.jwt.secret:defaultSecret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private Long jwtExpirationMs;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // Endpoints públicos de productos y categorías (solo lectura)
                .requestMatchers("GET", "/api/productos/**").permitAll()
                .requestMatchers("GET", "/api/categorias/**").permitAll()
                // Endpoints públicos para servicios y sedes (para el formulario)
                .requestMatchers("GET", "/api/servicios/**").permitAll()
                .requestMatchers("GET", "/api/sedes/**").permitAll()
                // Endpoints de administración (requieren autenticación)
                .requestMatchers("POST", "/api/productos/**").authenticated()
                .requestMatchers("PUT", "/api/productos/**").authenticated()
                .requestMatchers("DELETE", "/api/productos/**").authenticated()
                .requestMatchers("POST", "/api/categorias/**").authenticated()
                .requestMatchers("PUT", "/api/categorias/**").authenticated()
                .requestMatchers("DELETE", "/api/categorias/**").authenticated()
                // Carrito requiere autenticación
                .requestMatchers("/api/carrito/**").authenticated()
                // Pedidos requieren autenticación
                .requestMatchers("/api/pedidos/**").authenticated()
                .anyRequest().permitAll()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public Long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
