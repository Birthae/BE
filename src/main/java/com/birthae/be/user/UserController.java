package com.birthae.be.user;

import com.birthae.be.common.dto.ResponseMessage;
import com.birthae.be.security.UserDetailsImpl;
import com.birthae.be.user.dto.LoginRequestDto;
import com.birthae.be.user.dto.SignupRequestDto;
import com.birthae.be.user.entity.User;
import com.birthae.be.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@RequestBody SignupRequestDto request) {
        User createdUser = userService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getBirth()
        );

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(createdUser)
                .statusCode(HttpStatus.CREATED.value())
                .resultMessage("회원가입 성공")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(responseMessage);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        Map<String, String> tokens = userService.login(request.getEmail(), request.getPassword());

        // 액세스 토큰 쿠키 설정
        Cookie accessTokenCookie = new Cookie("accessToken", Base64.getUrlEncoder().encodeToString(tokens.get("accessToken").getBytes()));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);

        // 응답 헤더에 액세스 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, tokens.get("accessToken"));

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(tokens)
                .statusCode(HttpStatus.OK.value())
                .resultMessage("로그인 성공")
                .build();

        return ResponseEntity.ok().headers(headers).body(responseMessage);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseMessage> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(user)
                .statusCode(HttpStatus.OK.value())
                .resultMessage("유저 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin")
    public ResponseEntity<ResponseMessage> getAdminInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(user)
                .statusCode(HttpStatus.OK.value())
                .resultMessage("유저 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
