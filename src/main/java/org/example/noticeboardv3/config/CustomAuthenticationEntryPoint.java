package org.example.noticeboardv3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// 인증 실패(401) 시 호출되는 진입점 — 다른 에러와 같은 JSON 형식으로 응답
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 응답 상태와 타입 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());          // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");                        // 한글 깨짐 방지

        // GlobalExceptionHandler와 동일한 형식 {timestamp, status, error, message}
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", "인증이 필요합니다.");

        // JSON으로 직렬화해서 응답 본문에 직접 작성
        objectMapper.writeValue(response.getWriter(), body);
    }
}