package org.example.noticeboardv3.exception;

// 403 — 본인 글/댓글이 아닌데 수정·삭제 시도
// [변경] 원래 AccessDeniedException으로 만들려 했으나 Spring Security에 동명 클래스가 있어 충돌.
//        이름을 NotOwnerException으로 바꿔 혼동을 원천 차단.
public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String message) { super(message); }
}