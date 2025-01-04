package com.example.user_service.security;

import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final UserService userService;
    private final Environment env;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager,
                                           UserService userService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
//                                .requestMatchers("/users/**", "/users").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/health_check", "/welcome").permitAll()
                                .requestMatchers(antMatcher(HttpMethod.POST,"/users")).permitAll()
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers("/users", "/users/**").access((authentication, request) -> {
                                    String clientIp = request.getRequest().getRemoteAddr();
                                    log.info("client ip is = {}", clientIp);

                                    // 허용된 IP 리스트
                                    String[] allowedIps = {"127.0.0.1","172.20.10.2"};

                                    // IP가 허용된 리스트에 포함되어 있는지 확인
                                    boolean isAllowed = Arrays.asList(allowedIps).contains(clientIp);

                                    return new AuthorizationDecision(isAllowed);
                                })
//                                .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager)
                // 사용자 세션 저장하지 않음
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 응답 헤더에 X-Frame-Options 추가 - 클릭재킹 공격 방어 - 동일 출처만 <iframe> 로드 가능.
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilter(customAuthenticationFilter(authenticationManager));

        return http.build();
    }

    /**
     * authenticationManager bean 생성
     * @param http HttpSecurity
     * @return authenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserService userService,
                                                       BCryptPasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    /**
     * customAuthenticationFilter bean 생성
     * @return customAuthenticationFilter bean
     */
    @Bean
    public AuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        return authenticationFilter;
    }

    /**
     * 비밀번호 암호화 모듈
     * @return 암호화 bean
     */



}
