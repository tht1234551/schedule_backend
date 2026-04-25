package com.jhj.schedule.security;

import com.jhj.schedule.auth.dto.LoginRequestDto;
import com.jhj.schedule.user.domain.Authority;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtFilterTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .authority(Authority.ROLE_USER)
                .build();

        userRepository.insert(user);
    }

    @Test
    @DisplayName("JWT_인증_성공")
    void success() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("test@test.com", "1234");

        String token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        Assert.notNull(token, "token is null");

        mockMvc.perform(get("/api/hello2")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}