package com.example.accmarket.utils.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/register", "/auth/login", "/auth/logout").permitAll()
                    .requestMatchers(
                        "/core/make-advertisement",
                        "/core/edit-advertisement",
                        "/core",
                    ).permitAll()
                    .requestMatchers("/admin/**").authenticated()
                    .requestMatchers("/core/**").authenticated()
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}