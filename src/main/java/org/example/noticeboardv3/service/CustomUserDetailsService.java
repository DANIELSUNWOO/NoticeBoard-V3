package org.example.noticeboardv3.service;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Security가 인증 시 호출하는 단 하나의 메서드.
    // [주의] 파라미터 이름은 username이지만, 우리 도메인에선 loginId를 의미함
    //        (HTTP Basic의 hong:password123 에서 'hong'이 여기로 들어옴)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // loginId로 사용자 조회 → 없으면 UsernameNotFoundException (Security가 401로 처리)
        // [핵심] User가 UserDetails를 구현했으므로, 찾은 User를 그대로 반환하면 됨
        return userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }
}