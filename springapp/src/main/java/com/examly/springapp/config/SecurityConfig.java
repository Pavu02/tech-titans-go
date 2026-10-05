package com.examly.springapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

            @Override
            public String encode(CharSequence rawPassword) {
                return bCrypt.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (encodedPassword == null) return false;
                if (rawPassword.toString().equals(encodedPassword)) return true;
                try {
                    return bCrypt.matches(rawPassword, encodedPassword);
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .accessDeniedHandler(accessDeniedHandler)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/register", "/api/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/books").hasAnyAuthority("Admin", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyAuthority("Admin", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAnyAuthority("Admin", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyAuthority("Admin", "User", "ROLE_ADMIN", "ROLE_USER")

                .requestMatchers(HttpMethod.POST, "/api/bookrentalrequest").hasAnyAuthority("User", "ROLE_USER")
                .requestMatchers(HttpMethod.GET, "/api/bookrentalrequest/user/**").hasAnyAuthority("User", "ROLE_USER")
                .requestMatchers(HttpMethod.GET, "/api/bookrentalrequest/{id}").hasAnyAuthority("Admin", "User", "ROLE_ADMIN", "ROLE_USER")
                .requestMatchers(HttpMethod.GET, "/api/bookrentalrequest").hasAnyAuthority("Admin", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/bookrentalrequest/**").hasAnyAuthority("Admin", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/bookrentalrequest/**").hasAnyAuthority("User", "ROLE_USER")

                .requestMatchers(HttpMethod.POST, "/api/feedback").hasAnyAuthority("User", "ROLE_USER")
                .requestMatchers(HttpMethod.GET, "/api/feedback").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/feedback/user/**").hasAnyAuthority("User", "ROLE_USER")
                .requestMatchers(HttpMethod.GET, "/api/feedback/**").hasAnyAuthority("Admin", "User", "ROLE_ADMIN", "ROLE_USER")
                .requestMatchers(HttpMethod.DELETE, "/api/feedback/**").hasAnyAuthority("User", "ROLE_USER")

                .requestMatchers("/api/chat/**", "/api/faqs/**", "/api/errorlogs/**").permitAll()
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
