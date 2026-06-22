package org.example.noticeboardv3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // ===== 체인 1: API (HTTP Basic) — 먼저 검사 =====
    @Bean
    @Order(1)  // [핵심] API 체인을 먼저 검사
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // [핵심] 이 체인은 아래 경로들만 담당
                .securityMatcher("/users", "/users/**", "/posts/**", "/comments/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()  // 회원가입만 열기
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

    // ===== 체인 2: 화면 (폼 로그인) — 나중에 검사 (나머지 전부) =====
    @Bean
    @Order(2)
    public SecurityFilterChain viewSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // securityMatcher 없음 → 위 체인이 안 잡은 나머지 전부 (화면 경로)
                .authorizeHttpRequests(auth -> auth
                        // 로그인 페이지, 정적 자원, 회원가입 화면은 누구나 접근
                        .requestMatchers("/view/login", "/view/signup", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()  // 나머지 화면은 로그인 필요
                )
                .formLogin(form -> form
                        .loginPage("/view/login")           // 커스텀 로그인 페이지 경로
                        .loginProcessingUrl("/view/login")  // 로그인 폼이 제출될 경로 (POST)
                        .defaultSuccessUrl("/view/posts", true)  // 로그인 성공 시 이동
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/view/logout")
                        .logoutSuccessUrl("/view/login")
                );
        // [주의] 화면 체인은 CSRF를 끄지 않음 (폼 전송 보안에 필요)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}