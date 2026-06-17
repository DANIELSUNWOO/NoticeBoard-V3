package org.example.noticeboardv3.exception;

// 409 — 중복된 loginId로 가입 시도
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) { super(message); }
}