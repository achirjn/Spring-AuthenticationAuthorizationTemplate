package com.HTT.backend.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.HTT.backend.services.UserService;

@Configuration
public class SecurityConfig {

    private final OAuthAuthenticationSuccessHandler oAuthSuccessHandler;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;


    public SecurityConfig(OAuthAuthenticationSuccessHandler oAuthSuccessHandler, UserDetailsService userDetailsService, JwtUtil jwtUtil, UserService userService){
    this.oAuthSuccessHandler = oAuthSuccessHandler;
       this.jwtUtil=jwtUtil;
       this.userDetailsService=userDetailsService;
       this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(List.of(daoAuthenticationProvider(),jwtAuthenticationProvider()));
    }
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(){
        return new JwtAuthenticationProvider(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, JwtUtil jwtUtil) throws Exception{
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil, userService);
        JwtValidationFilter jwtValidationFilter = new JwtValidationFilter(authenticationManager);
        http
            .cors(withDefaults())
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/login/oauth2/**").permitAll()
                // .anyRequest().permitAll()
                .anyRequest().authenticated()
                )
            .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtValidationFilter, JwtAuthenticationFilter.class);
        http.oauth2Login(oauth -> {
            // oauth.loginPage("https://www.thinkindiasvnit.in/login");
            oauth.successHandler(oAuthSuccessHandler);
        });
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // This is the origin of your React app
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8082")); 
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
