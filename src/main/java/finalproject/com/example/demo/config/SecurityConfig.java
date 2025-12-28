package finalproject.com.example.demo.config;

import finalproject.com.example.demo.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth

                // AUTH
                .requestMatchers("/auth/**").permitAll()
                // PUBLIC
                .requestMatchers(HttpMethod.GET,
                        "/products/**",
                        "/categories/**",
                        "/reviews/**"
                ).permitAll()

                // ADMIN USERS
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                // PRODUCTS (ADMIN + SELLER)
                .requestMatchers(HttpMethod.POST, "/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")
                .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")

                // ORDER ITEMS
                .requestMatchers(HttpMethod.GET, "/order-items/**").hasAuthority("ROLE_ADMIN")
                //.requestMatchers(HttpMethod.POST, "/order-items", "/order-items/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/order-items", "/order-items/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/order-items", "/order-items/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/order-items", "/order-items/**").hasAuthority("ROLE_ADMIN")

                // ORDERS & USERS
                .requestMatchers("/orders/**", "/users/**").authenticated()

                // EVERYTHING ELSE → AUTHENTICATED ONLY
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}


//package finalproject.com.example.demo.config;
//
//import finalproject.com.example.demo.security.jwt.JwtAuthFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    private final JwtAuthFilter jwtAuthFilter;
//
//    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
//        this.jwtAuthFilter = jwtAuthFilter;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf.disable());
//
//        http.sessionManagement(sm ->
//                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//
//        http.authorizeHttpRequests(auth -> auth
//
//                // AUTH
//                .requestMatchers("/auth/**").permitAll()
//                // PUBLIC
//                .requestMatchers(HttpMethod.GET,
//                        "/products/**",
//                        "/categories/**",
//                        "/reviews/**"
//                ).permitAll()
//
//                // EVERYTHING ELSE → AUTHENTICATED ONLY
//                .anyRequest().authenticated()
//        );
//
//        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration config
//    ) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}