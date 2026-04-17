package com.jhj.schedule.security;

import com.jhj.schedule.auth.dto.LoginRequestDto;
import com.jhj.schedule.user.Authority;
import com.jhj.schedule.user.UserEntity;
import com.jhj.schedule.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
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
        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .authority(Authority.ROLE_USER)
                .build();

        userRepository.save(user);
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

        mockMvc.perform(get("/api/hello2")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}