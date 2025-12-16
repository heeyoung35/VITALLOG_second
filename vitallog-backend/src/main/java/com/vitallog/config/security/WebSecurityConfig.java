package com.vitallog.config.security;

import com.vitallog.config.security.filter.CustomAuthenticationFilter;
import com.vitallog.config.security.filter.JwtAuthorizationFilter;
import com.vitallog.config.security.handler.CustomAuthFailureHandler;
import com.vitallog.config.security.handler.CustomAuthSuccessHandler;
import com.vitallog.config.security.handler.CustomAuthenticationProvider;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthSuccessHandler customAuthSuccessHandler;
    private final CustomAuthFailureHandler customAuthFailureHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**")
                // 추가: /payment.html, /index.html 등 루트 경로의 HTML 파일을 무시
                .requestMatchers("/*.html")
                .requestMatchers("/api/order/**")
                // 추가: /static/ 하위 경로에 대한 명시적 무시 (safety measure)
                .requestMatchers("/static/**")
                // 추가: /pay/ 폴더를 포함한 /static/ 하위 모든 HTML/파일
                .requestMatchers("/pay/**")//  /pay/success.html, /pay/fail.html 등에 적용
                // 추가: 결제/주문 최종 처리 API 무시 (토큰 검사 제외)
                .requestMatchers(
                        "/api/pay/confirm",
                        "/api/order/success",
                        "/api/order/fail"
                );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // POST 요청 테스트를 위해 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 요청 허용 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        /* ⭐⭐⭐ 현재 '.hasRole'을 사용하면 관리자 권한을 얻는 데 실패하는 문제가 있어서 '.hasAuthority'로 변경해둔 상태로, 혹시라도 관련 문제가 생긴다면 '.hasRole'로 시도해보기 ⭐⭐⭐ */
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN") // /api/admin/ 하위는 관리자만!
                        .requestMatchers(HttpMethod.POST, "/supplement").permitAll()
                        .requestMatchers(HttpMethod.GET, "/supplement/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cart/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cartItem/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/recommend/**").permitAll()
                        .requestMatchers(
                                "/api/user/signup",
                                "/api/user/login",
                                "/api/user/reissue", // 재발급도 보통 토큰 만료 시 요청하므로 열어두거나 RefreshToken만 검사
                                "/api/user/find-id",
                                "/api/user/reset-password",
                                "/api/user/check-id",
                                "/api/user/check-email").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthorizationFilter(), BasicAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

    private JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(authenticationManager());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }


    public CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationManager());

        // 로그인 요청을 처리할 URL 설정
        customAuthenticationFilter.setFilterProcessesUrl("/login");

        // 로그인 성공과 실패시 핸들러 등록
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthSuccessHandler);
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthLoginFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;

    }

    private CustomAuthSuccessHandler customAuthLoginSuccessHandler() {
        return new CustomAuthSuccessHandler();
    }

    private CustomAuthFailureHandler customAuthLoginFailureHandler() {

        return new CustomAuthFailureHandler();
    }

}
