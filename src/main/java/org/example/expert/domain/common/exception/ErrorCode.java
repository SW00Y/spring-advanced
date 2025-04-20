package org.example.expert.domain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public enum ErrorCode {

    TEST_ERROR("TEST-001", HttpStatus.NOT_FOUND,"테스트 에러"),
    TEST_ERROR2("TEST-002", HttpStatus.NOT_FOUND,"테스트 에러"),

    //AUTH
    ALREADY_MAIL("AUTH-001",HttpStatus.BAD_REQUEST,"이미 존재하는 이메일입니다."),
    NOT_FOUND_EMAIL("AUTH-002",HttpStatus.NOT_FOUND,"가입되지 않은 유저입니다."),
    WRONG_PASSWORD("AUTH-003",HttpStatus.UNAUTHORIZED,"잘못된 비밀번호입니다."),


    //TODO
    NOT_FOUND_TODO("TODO-001",HttpStatus.NOT_FOUND,"일정을 찾을 수 없습니다."),

    //USER
    NOT_FOUND_USER("USER-001",HttpStatus.NOT_FOUND,"유저를 찾을 수 없습니다."),
    PASSWORD_UNCHANGED("USER-002",HttpStatus.BAD_REQUEST,"새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),

    //MANAGER
    INVALID_CREATE_USER("MANAGER-001", HttpStatus.NOT_FOUND, "담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다."),
    INVALID_TODO_USER("MANAGER-002",HttpStatus.NOT_FOUND, "해당 일정을 만든 유저가 유효하지 않습니다."),
    ASSIGN_USER_NOT_FOUND("MANAGER-003", HttpStatus.NOT_FOUND, "등록하려고 하는 담당자 유저가 존재하지 않습니다."),
    MANAGER_NOT_FOUND("MANAGER-004", HttpStatus.NOT_FOUND, "담당자를 찾을 수 없습니다."),
    CANNOT_SELF_MANAGER("MANAGER-005", HttpStatus.BAD_REQUEST, "일정 작성자는 본인을 담당자로 등록할 수 없습니다."),
    NOT_ASSIGN_MANAGER("MANAGER-006", HttpStatus.FORBIDDEN, "해당 일정에 등록된 담당자가 아닙니다."),

    //USER_ROLE
    INVALID_USER_ROLE("ROLE-001",HttpStatus.BAD_REQUEST,"유효하지 않은 UerRole"),

    //WEATHER
    NOT_FOUND_WEATHER("WEATHER-001",HttpStatus.NOT_FOUND,"날씨 데이터가 없습니다."),
    FAIL_GET_WEATHER("WEATHER-002",HttpStatus.NOT_FOUND,"날씨 데이터를 가져오는데 실패했습니다."),
    NOT_FOUND_TODAY_WEATHER("WEATHER-003",HttpStatus.NOT_FOUND,"오늘에 해당하는 날씨 데이터를 찾을 수 없습니다."),

    //JWT
    NOT_FOUND_JWT("JWT-001",HttpStatus.NOT_FOUND,"Not Found Token");


    private final String code;
    private final HttpStatus status;
    private final String message;
    private final LocalDateTime localDateTime;

    ErrorCode(String code, HttpStatus status, String message)
    {
        this.code = code;
        this.status = status;
        this.message = message;
        this.localDateTime = LocalDateTime.now();
    }

}
