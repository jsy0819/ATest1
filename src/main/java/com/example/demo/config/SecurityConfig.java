package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/", "/users/signup", "/users/login",
                    "/stores/**", 
                    "/h2-console/**",
                    "/uploaded-images/**",
                    "/css/**",
                    "/js/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            .formLogin(formLogin -> formLogin
                .loginPage("/users/login")
                .loginProcessingUrl("/users/login")
                .defaultSuccessUrl("/stores", true)
                .failureUrl("/users/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/users/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
            )

            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))

            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler())
            )
            
            // 세션 관리 설정 추가
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .and()
                .sessionFixation().migrateSession()
            );

        return http.build();
    }
}