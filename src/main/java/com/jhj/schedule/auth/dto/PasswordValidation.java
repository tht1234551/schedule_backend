package com.jhj.schedule.auth.dto;

public final class PasswordValidation {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 64;
    public static final String LENGTH_MESSAGE = "비밀번호는 8~64자여야 합니다.";
    public static final String PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
    public static final String PATTERN_MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.";

    private PasswordValidation() {}
}
