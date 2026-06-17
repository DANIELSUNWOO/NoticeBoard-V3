package org.example.noticeboardv3.exception;

// 404 — 사용자를 찾을 수 없음
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
}