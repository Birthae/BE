package com.birthae.be.user;

import com.birthae.be.user.entity.User;
import com.birthae.be.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User signup(String email, String password, String name) {
        User newMember = new User(email, passwordEncoder.encode(password), name);
        return userRepository.save(newMember);
    }

    public Map<String, String> login(String email, String password) {
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String accessToken = jwtUtil.createToken(email, member.getRole().name());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        return tokens;
    }
}
