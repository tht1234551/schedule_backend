package com.jhj.schedule.job.dto;

public final class JobValidation {

    public static final String HEX_COLOR_PATTERN = "^#[0-9a-fA-F]{6}$";
    public static final String HEX_COLOR_MESSAGE = "hexColor는 #RRGGBB 형식이어야 합니다.";
    public static final int TITLE_MAX = 255;
    public static final int DESCRIPTION_MAX = 500;

    private JobValidation() {}

}
