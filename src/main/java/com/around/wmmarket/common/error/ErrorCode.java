package com.around.wmmarket.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 200 OK : 정상 */
    /* 201 CREATED : 정상, 데이터가 생성됨(POST) */
    /* 202 Accepted : 정상, 클라이언트의 요청은 정상적이나 서버가 아직 요청을 완료하지 못했다. */
    /* 204 NO CONTENT : 정상, Response Body 가 아예 없는것, 현재는 아무것도 해당 안됨 */

    /* 400 BAD_REQUEST : 실패, 클라이언트에서 넘어온 파라미터가 이상함 */
    // User
    DUPLICATE_USER_EMAIL(BAD_REQUEST,"U001","중복된 회원 이메일이 있습니다."),
    DUPLICATE_SIGN_IN(BAD_REQUEST,"U002","이미 로그인 한 상태입니다."),
    USER_NOT_FOUND(BAD_REQUEST, "U003","유저 정보를 찾을 수 없습니다."),
    USER_IMAGE_NOT_FOUND(BAD_REQUEST,"U004","유저 이미지를 찾을 수 없습니다."),
    INVALID_USER_PASSWORD(BAD_REQUEST,"U005","비밀번호가 일치하지 않습니다."),
    SIGNED_USER_NOT_FOUND(BAD_REQUEST,"U006","로그인한 유저를 찾을 수 없습니다."),

    // Common
    INVALID_TYPE_VALUE(BAD_REQUEST, "C001", "Invalid Type Value"),

    /* 401 UNAUTHORIZED : 인증(UNAUTHENTICATED)되지 않은 사용자 */
    /* 403 FORBIDDEN :  권한이 없는 사용자(UNAUTHORIZED) */
    // Post
    UNAUTHORIZED_USER_TO_DEALPOST(FORBIDDEN,"P001","거래글에대한 권한이 없습니다."),

    /* 404 NOT_FOUND : 실패, 데이터가 있어야 하나 없음 */

    /* 500 INTERNAL_SERVER_ERROR : 내부 서버 에러 */
    // Server
    UNDEFINED_ERROR(INTERNAL_SERVER_ERROR, "S001", "정의되지 않은 에러입니다."),
    FILE_NOT_FOUND(INTERNAL_SERVER_ERROR,"S002","파일을 읽을 수 없습니다."),
    DELETE_FAIL(INTERNAL_SERVER_ERROR,"S003","파일을 삭제할 수 없습니다."),
    MEDIA_TYPE_NOT_FOUND(INTERNAL_SERVER_ERROR,"S004","미디어 파일 타입이 유효하지 않습니다."),
    /* 501 Not Implemented : 실패, 없는 리소스 요청 */
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
