package com.jhj.schedule.auth.security.jwt;

import com.jhj.schedule.auth.exception.ExpiredTokenException;
import com.jhj.schedule.auth.exception.InvalidTokenException;
import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.exception.UserErrorCode;
import com.jhj.schedule.user.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@NullMarked
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION))
                .orElse(null);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long id = jwtUtil.getUserId(token);
            User user = userService.findById(id);

            CustomUserDetail customUserDetails = new CustomUserDetail(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            resolver.resolveException(request, response, null, e);
            return;
        } catch (UserNotFoundException e) {
            resolver.resolveException(request, response, null, new UserNotFoundException(UserErrorCode.UNAUTHORIZED, e));
            return;
        }

        filterChain.doFilter(request, response);
    }
}