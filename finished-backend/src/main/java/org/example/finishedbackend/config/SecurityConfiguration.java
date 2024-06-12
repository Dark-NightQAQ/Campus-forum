package org.example.finishedbackend.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.entity.VO.response.AuthorizeVO;
import org.example.finishedbackend.filter.AuthenticationFilter;
import org.example.finishedbackend.service.AccountService;
import org.example.finishedbackend.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@Slf4j
public class SecurityConfiguration {

    @Resource
    AuthenticationFilter authenticationFilter;

    @Resource
    JwtUtils jwtUtils;

    @Resource
    AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(config -> {
                    config.requestMatchers("/api/auth/**", "/api/user/**").permitAll();
                    config.anyRequest().authenticated();
                })
                .formLogin(conf -> {
                    conf.loginProcessingUrl("/api/auth/login");
                    conf.successHandler(this::onAuthenticationSuccess);
                    conf.failureHandler(this::onAuthenticationFailure);
                })
                .logout(conf -> {
                    conf.logoutUrl("/api/auth/logout");
                    conf.logoutSuccessHandler(this::onLogoutSuccess);
                })
                .exceptionHandling(conf -> {
                    conf.accessDeniedHandler(this::handle);
                    conf.authenticationEntryPoint(this::commence);
                })
                .sessionManagement(conf -> {
                    conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        User user = (User) authentication.getPrincipal();
        AccountDTO accountDTO = accountService.query().eq("username", user.getUsername()).one();
        String token = jwtUtils.createToken(user, accountDTO.getId(), user.getUsername());
        AuthorizeVO vo = new AuthorizeVO(user.getUsername(), accountDTO.getRole(), token, jwtUtils.expireDate());
        writer.write(RestBean.success(vo, "登录成功").asJSONString());
    }

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.failure(400, exception.getMessage()).asJSONString());
    }

    void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (jwtUtils.deleteToken(authorization)) {
            writer.write(RestBean.success("退出登录成功").asJSONString());
        } else {
            writer.write(RestBean.failure(401, "退出登录异常").asJSONString());
        }

    }

    void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.failure(403, accessDeniedException.getMessage()).asJSONString());
    }

    void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.failure(401, authException.getMessage()).asJSONString());
    }
}
