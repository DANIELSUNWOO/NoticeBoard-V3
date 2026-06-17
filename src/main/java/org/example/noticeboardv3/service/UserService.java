package org.example.noticeboardv3.service;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv3.domain.Role;
import org.example.noticeboardv3.domain.User;
import org.example.noticeboardv3.dto.UserDto;
import org.example.noticeboardv3.exception.DuplicateUserException;
import org.example.noticeboardv3.exception.UserNotFoundException;
import org.example.noticeboardv3.repository.RoleRepository;
import org.example.noticeboardv3.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;        // [추가] USER 역할 조회/생성용
    private final PasswordEncoder passwordEncoder;      // [추가] 비밀번호 암호화용 (SecurityConfig의 빈이 주입됨)

    @Transactional
    public User createUser(UserDto.SignUpRequest request) {
        // 1. loginId 중복 확인 → 있으면 409
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new DuplicateUserException("이미 사용 중인 로그인 ID입니다.");
        }

        // 2. User 생성 — 비밀번호는 반드시 암호화해서 저장 (평문 금지)
        User user = User.builder()
                .name(request.name())
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))  // [핵심] 평문 → BCrypt 암호화
                .email(request.email())
                .build();

        // 3. 기본 "USER" 역할 부여 — DB에 없으면 새로 만들어서 사용 (실습 6-5 방식)
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
        user.addRole(userRole);

        // 4. 저장 후 반환
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        // 존재 확인 → 없으면 404
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + id));
        // [주의] "본인 또는 ADMIN만 삭제" 권한 검증은 컨트롤러에서 인증 정보와 함께 처리 예정.
        //        여기선 일단 존재 확인 + 삭제까지만.
        userRepository.delete(user);
    }
}