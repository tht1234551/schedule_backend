package com.jhj.schedule.group.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    private Long id;
    private String groupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}