package com.jhj.schedule.common.config;

import org.jooq.conf.ExecuteWithoutWhere;
import org.springframework.boot.jooq.autoconfigure.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return configuration -> configuration.settings()
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)  // 조건절 없이 delete 발생시 예외 발생
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)  // 조건절 없이 update 발생시 예외 발생
                .withRenderSchema(false)  // 스키마 이름을 sql 에 포함 하지 않음
                .withRenderFormatted(true)  // SQL 로깅 포맷팅
                .withBatchSize(100) // 배치 크기 설정
                .withQueryTimeout(60); // SQL 실행 타임아웃;
    }

//    @Bean
//    public DefaultExecuteListenerProvider customListener() {
//        return new DefaultExecuteListenerProvider(new DefaultExecuteListener() {
//
//            private long startTime;
//
//            @Override
//            public void executeStart(ExecuteContext ctx) {
//                startTime = System.currentTimeMillis();
//            }
//
//            @Override
//            public void executeEnd(ExecuteContext ctx) {
//                long time = System.currentTimeMillis() - startTime;
//
//                String sql = ctx.query() != null
//                        ? DSL.using(dialect, new Settings().withRenderFormatted(true))
//                          .renderInlined(ctx.query())
//                        : ctx.sql();
//
//                System.out.printf("[JOOQ] 실행 시간: %dms\nSQL: %s", time, sql);
//            }
//        });
//    }

}