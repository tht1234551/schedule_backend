package com.jhj.schedule.auth.security.jwt;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.domain.Authority;
import com.jhj.schedule.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;


    // TODO: 정리
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 토큰 값만 추출
        String token = authorizationHeader.substring(7);

        try {
            if (jwtUtil.invalid(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }

            // JWT 만료 여부 검증
            if (jwtUtil.isExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
                return;
            }

            // JWT에서 사용자 정보 추출
            String email = jwtUtil.getEmail(token);

            // redis 유저
            User user = userService.findByEmail(email);

            // jwt 유저
//            User user = User.builder()
//                    .id(id)
//                    .email(email)
//                    .password(null)
//                    .authority(Authority.valueOf(role))
//                    .build();

            CustomUserDetail customUserDetails = new CustomUserDetail(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // SecurityContext에 인증 정보 저장 (STATLESS 모드이므로 요청 종료 시 소멸)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        filterChain.doFilter(request, response);
    }
}
