package com.jhj.schedule.common.config;

import com.jhj.schedule.auth.infrastructure.RefreshTokenRepository;
import com.jhj.schedule.auth.security.jwt.JwtFilter;
import com.jhj.schedule.auth.security.jwt.JwtUtil;
import com.jhj.schedule.auth.security.jwt.LoginFilter;
import com.jhj.schedule.auth.security.userdetail.CustomUserDetailService;
import com.jhj.schedule.common.util.ActiveProfileUtil;
import com.jhj.schedule.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final ActiveProfileUtil activeProfileUtil;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 프론트 CORS 헤더 세팅
                .csrf(CsrfConfigurer::disable) // REST API라면 비활성화
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/api/auth/**",
                            "/actuator",
                            "/actuator/health",
                            "/error",
                            "/favicon.ico"
                    ).permitAll();

                    if (activeProfileUtil.isNotProd()) {
                        auth.requestMatchers(
                                "/actuator",
                                "/api/hello2",
                                "/api/hello3",
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/h2-console/**"
                        ).permitAll();
                    }

                    auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight 허용
                            .anyRequest().authenticated();                   // 나머지는 인증 필요

                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 사용 시
                )
//                .anonymous(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // ← H2 콘솔 iframe 허용
                );


        LoginFilter loginFilter = new LoginFilter(authenticationManager, refreshTokenRepository, jwtUtil);
        loginFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                .addFilterBefore(new JwtFilter(jwtUtil, userService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationManagerBuilder authenticationManagerBuilder) {
//        authenticationManagerBuilder
//                .userDetailsService(customUserDetailService)
//                .passwordEncoder(passwordEncoder());
//
//        return authenticationManagerBuilder.build();
//    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // 허용할 도메인
        configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용
        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Collections.singletonList("Authorization")); // Authorization 헤더 노출
        configuration.setMaxAge(3600L); // 1시간 동안 캐싱

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로 적용

        return source;
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers("/error", "/favicon.ico");
//    }
}
