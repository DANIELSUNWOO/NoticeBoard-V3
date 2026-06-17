package org.example.noticeboardv3.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — NotFound 3종 묶음
    @ExceptionHandler({
            UserNotFoundException.class,
            PostNotFoundException.class,
            CommentNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409 — 중복 loginId
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateUserException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 403 — 소유권 위반 (우리 커스텀 예외)
    @ExceptionHandler(NotOwnerException.class)
    public ResponseEntity<ErrorResponse> handleNotOwner(NotOwnerException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // 400 — @Valid 검증 실패 (필수 필드 누락 등)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("잘못된 요청입니다.");
        return build(HttpStatus.BAD_REQUEST, message);
    }

    // 500 — 그 외 모든 예외의 그물
    // [주의] 401(인증 실패)/403(Security 레벨)은 여기 안 옴 — Security 필터에서 먼저 처리됨 (다음 단계에서 설정)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 서버 오류가 발생했습니다.");
    }

    // 공통 응답 빌더 — 테스트 문서 형식 {timestamp, status, error, message}
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),              // 숫자 (404, 409 ...)
                status.getReasonPhrase(),    // 표준 문구 ("Not Found", "Conflict" ...)
                message                      // 우리가 던진 구체적 메시지
        );
        return ResponseEntity.status(status).body(body);
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
    }
}