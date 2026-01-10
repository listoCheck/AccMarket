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

//    @Bean
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .csrf { csrf -> csrf.disable() }
//            .authorizeHttpRequests { auth ->
//                auth
//                    .requestMatchers("/auth/register", "/auth/login", "/auth/logout").permitAll()
//
//                    .requestMatchers("/admin/**").permitAll()
//
//                    .requestMatchers("/core/**").permitAll()
//
//                    .requestMatchers(
//                        "/notifications/**"
//                    ).permitAll()
//
//                    .requestMatchers(
//                        "/appeals/**"
//                    ).permitAll()
//
//                    .requestMatchers(
//                        "/admin/moderation/**"
//                    ).permitAll()
//
//                    .requestMatchers(
//                        "/balance/**"
//                    ).permitAll()
//
//                    .requestMatchers(
//                        "/swagger-ui/**",
//                        "/v3/api-docs/**",
//                        "/swagger-ui.html"
//                    ).permitAll()
//
//                    .anyRequest().authenticated()
//            }
//        return http.build()
//    }
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()  // Разрешаем ВСЕ запросы
            }
        return http.build()
    }
}
