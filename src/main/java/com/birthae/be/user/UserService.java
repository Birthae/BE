package com.birthae.be.user;

import com.birthae.be.common.exception.BizRuntimeException;
import com.birthae.be.user.entity.User;
import com.birthae.be.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User signup(String email, String password, String name, LocalDate birth) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BizRuntimeException("이미 존재하는 이메일입니다.");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setName(name);
        newUser.setBirth(birth);
        return userRepository.save(newUser);
    }

    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BizRuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizRuntimeException("Invalid email or password");
        }

        String accessToken = jwtUtil.createToken(email, user.getRole().name());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        return tokens;
    }
}
