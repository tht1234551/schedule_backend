package com.jhj.schedule.auth.security.jwt;

import com.jhj.schedule.auth.application.AuthService;
import com.jhj.schedule.auth.dto.IssuedTokens;
import com.jhj.schedule.auth.dto.request.LoginRequestDto;
import com.jhj.schedule.auth.exception.AuthErrorCode;
import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.common.exception.CustomRuntimeException;
import com.jhj.schedule.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@NullMarked
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;
    private final ObjectMapper objectMapper;
    private final HandlerExceptionResolver resolver;


    /**
     * 로그인 요청 시 사용자 인증 처리
     */
    @Override
    @NonNull
    public Authentication attemptAuthentication(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws AuthenticationException {
        LoginRequestDto dto;

        try {
            dto = objectMapper.readValue(req.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login request", e);
        }

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

        return authenticationManager.authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth
    ) {
        CustomUserDetail customUserDetail = (CustomUserDetail) auth.getPrincipal();
        //noinspection DataFlowIssue
        Long userId = customUserDetail.getUser().getId();

        try {
            IssuedTokens tokens = authService.issueTokens(userId);
            ResponseCookie refreshCookie = refreshTokenCookieFactory.issue(tokens.getRefreshToken(), request.isSecure());

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken());
            response.setStatus(HttpStatus.OK.value());
        } catch (CustomRuntimeException e) {
            resolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, new CustomRuntimeException(ErrorCode.INTERNAL_SERVER_ERROR, e));
        }
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) {
        resolver.resolveException(request, response, null, new CustomRuntimeException(AuthErrorCode.LOGIN_FAIL, failed));
    }
}