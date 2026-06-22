package org.example.noticeboardv3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // 로그인 화면 — GET /view/login
    @GetMapping("/view/login")
    public String loginPage() {
        // "login"을 반환 → templates/login.html 파일을 찾아서 보여줌
        return "login";
    }
}