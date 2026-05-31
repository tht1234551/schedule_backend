package com.jhj.schedule.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    // RedisConnectionFactory는 Spring Boot가 spring.data.redis.* (host/port/username/password)로
    // 자동 구성한다. 직접 만들면 username/password가 누락되어 인증(NOAUTH) 에러가 발생하므로
    // 여기서는 자동 구성된 팩토리를 주입만 받는다.
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}