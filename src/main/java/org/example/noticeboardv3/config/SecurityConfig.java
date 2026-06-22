package org.example.noticeboardv3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor   // EntryPoint 주입 (6.22 add)

public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;  // (6.22 add)


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 — 순수 REST API는 세션/쿠키 기반이 아니라 매 요청 인증 헤더를 쓰므로 불필요
                //        (지난번 Thymeleaf 폼에서 겪은 CSRF 토큰 이슈가 여기선 안 생김)
                .csrf(csrf -> csrf.disable())

                // 세션을 만들지 않음(STATELESS) — HTTP Basic은 매 요청마다 인증하므로 서버가 상태를 안 가짐
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // [주의] 회원가입(POST /users)만 인증 없이 허용. HttpMethod로 정확히 POST만 열기.
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/users").permitAll()
                        // 나머지 모든 요청은 인증 필요 (목록 조회까지 전부 — 명세대로)
                        .anyRequest().authenticated()
                )

                // [결정] HTTP Basic 인증 활성화
                .httpBasic(Customizer.withDefaults())
                // 인증 실패 시 우리 EntryPoint가 처리하도록 등록 (6.22 add)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

    // 비밀번호 암호화기 — BCrypt (명세 요구사항)
    // [핵심] 이 빈이 등록되면, 가입 시 암호화와 로그인 시 비교에 모두 같은 인코더가 쓰임
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}