package com.dmadev.storage.config;

import com.dmadev.storage.service.JwtService;
import com.dmadev.storage.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            // Если заголовок авторизации пустой или не начинается с префикса "Bearer",
            // продолжаем выполнение цепочки фильтров без изменений
            filterChain.doFilter(request, response);
            return;
        }
        // Обрезаем префикс и получаем имя пользователя из токена
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var username = jwtService.extractUserName(jwt);

        boolean isNotAuthenticated = SecurityContextHolder.getContext().getAuthentication() == null;
        if (StringUtils.isNotEmpty(username) && isNotAuthenticated) {
            UserDetails principal = userService.userDetailsService()
                    .loadUserByUsername(username);

            //валидность токена с учетом загруженных деталей пользователя
            if (jwtService.isTokenValid(jwt, principal)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                // Создаем объект аутентификации на основе деталей пользователя
                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
                // Устанавливаем дополнительные детали аутентификации, такие как источник запроса
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Устанавливаем аутентификацию в контексте безопасности
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request,response);
    }


}
