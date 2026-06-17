package org.example.noticeboardv3.exception;

// 404 — 댓글을 찾을 수 없음
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) { super(message); }
}