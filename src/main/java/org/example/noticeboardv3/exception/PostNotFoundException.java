package org.example.noticeboardv3.exception;

// 404 — 게시글을 찾을 수 없음
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) { super(message); }
}