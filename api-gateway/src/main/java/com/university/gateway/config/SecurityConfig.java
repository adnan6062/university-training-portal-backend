package com.university.gateway.config;

import com.university.gateway.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Prevent Spring Boot from auto-registering JwtAuthFilter as a raw servlet
     * filter (which would run it twice). It is registered only via the Security
     * chain through addFilterBefore below.
     */
    
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            /*
             * WHY anyRequest().permitAll() at the gateway?
             *
             * Spring Security 6 uses MvcRequestMatcher by default. MvcRequestMatcher
             * resolves paths through HandlerMappingIntrospector — it only matches
             * if a Spring MVC @Controller / RouterFunction handler is registered for
             * that exact path.
             *
             * Gateway PROXY routes (e.g. /identity/**, /catalog/**) are NOT
             * registered as MVC handlers. They are handled by Spring Cloud Gateway's
             * RoutePredicateHandlerMapping at a lower level. This means any Spring
             * Security requestMatchers rule targeting a proxied path silently NEVER
             * matches, causing every proxied request to fall through to
             * anyRequest().authenticated() and return 403.
             *
             * The solution: disable Spring Security's authorization check at the
             * gateway level entirely (anyRequest().permitAll()) and rely exclusively
             * on the JwtAuthFilter for JWT enforcement. The JwtAuthFilter:
             *   • skips all Swagger / actuator / auth paths via shouldNotFilter()
             *   • validates Bearer tokens and returns 401 for everything else
             * Each downstream microservice ALSO validates the JWT independently,
             * so security is enforced end-to-end even without gateway-level rules.
             */
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
