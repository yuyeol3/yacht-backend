package io.github.yuyeol3.yachtbackend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "리소스를 찾을 수 없습니다."),

    // User
    ID_OCCUPIED(HttpStatus.BAD_REQUEST, "U001", "사용 중인 아이디입니다."),
    NICKNAME_OCCUPIED(HttpStatus.BAD_REQUEST, "U002", "사용 중인 닉네임입니다."),
    ID_EQUALS_NICKNAME(HttpStatus.BAD_REQUEST, "U003", "아이디와 닉네임은 달라야 합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U004", "존재하지 않는 사용자입니다."),


    // Auth
    INVALID_ID_OR_PWD(HttpStatus.NOT_FOUND, "A001", "아이디나 비밀번호에 맞는 사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A002", "인증되지 않았습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
