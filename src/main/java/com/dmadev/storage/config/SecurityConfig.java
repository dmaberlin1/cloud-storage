package com.dmadev.storage.config;

import com.dmadev.storage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
       http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors->cors.configurationSource(request->{
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*"));// Разрешение запросов со всех доменов
                    corsConfiguration.setAllowedMethods(List.of("GET","POST","DELETE","OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true); // Разрешение передачи куки
                    return corsConfiguration;
                }))
                  // Настройка доступа к конечным точкам
                  .authorizeHttpRequests(request->request
                          // Можно указать конкретный путь, * - 1 уровень вложенности, ** - любое количество уровней вложенности
                          .requestMatchers("/auth/**").permitAll()
                          .requestMatchers("/swagger-ui/**","/swagger-resources/*","/v3/api-docs/**").permitAll()
                          // Доступ к путям /endpoint и /admin/** только для пользователей с ролью ADMIN
                          .requestMatchers("/endpoint","/admin/**").hasRole("ADMIN")
                          .anyRequest().authenticated()) // Все остальные запросы требуют аутентификации
               .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authenticationProvider(authenticationProvider())
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);



               return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService.userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Получение и возвращение менеджера аутентификации из конфигурации
        return config.getAuthenticationManager();
    }
}
