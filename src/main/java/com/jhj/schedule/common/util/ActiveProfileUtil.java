package com.jhj.schedule.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ActiveProfileUtil {

    private final Environment environment;

    public boolean isProd() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    public boolean isDev() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}
