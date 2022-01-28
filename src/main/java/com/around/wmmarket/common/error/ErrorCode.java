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
    /* 401 UNAUTHORIZED : 인증(UNAUTHENTICATED)되지 않은 사용자 */
    /* 403 FORBIDDEN :  권한이 없는 사용자(UNAUTHORIZED) */
    /* 404 NOT_FOUND : 실패, 데이터가 있어야 하나 없음 */

    /* 500 INTERNAL_SERVER_ERROR : 내부 서버 에러 */
    /* 501 Not Implemented : 실패, 없는 리소스 요청 */

    // User
    DUPLICATED_USER_EMAIL(BAD_REQUEST,"U001","중복된 회원 이메일이 있습니다."),
    DUPLICATED_SIGN_IN(BAD_REQUEST,"U002","이미 로그인 한 상태입니다."),
    USER_NOT_FOUND(BAD_REQUEST, "U003","유저 정보를 찾을 수 없습니다."),
    USER_IMAGE_NOT_FOUND(BAD_REQUEST,"U004","유저 이미지를 찾을 수 없습니다."),
    INVALID_USER_PASSWORD(BAD_REQUEST,"U005","비밀번호가 일치하지 않습니다."),
    SIGNED_USER_NOT_FOUND(BAD_REQUEST,"U006","로그인한 유저를 찾을 수 없습니다."),
    UNAUTHORIZED_USER_TO_USER(FORBIDDEN,"U007","유저에 대한 권한이 없습니다."),
    DUPLICATED_USER_LIKE(BAD_REQUEST,"U008","이미 좋아요를 누른 글입니다."),
    USER_LIKE_NOT_FOUND(BAD_REQUEST,"U009","좋아요를 찾을 수 없습니다."),
    DUPLICATED_USER_AUTH(BAD_REQUEST,"U010","이미 인증된 회원입니다."),
    INVALID_AUTH_CODE(BAD_REQUEST,"U011","인증 코드가 일치하지 않습니다."),
    DUPLICATED_USER_NICKNAME(BAD_REQUEST,"U012","중복된 회원 닉네임이 있습니다."),

    // Deal
    UNAUTHORIZED_USER_TO_DEALPOST(FORBIDDEN,"D001","거래글에대한 권한이 없습니다."),
    DEALPOST_NOT_FOUND(BAD_REQUEST,"D002","거래글을 찾을 수 없습니다."),
    DEALPOST_IMAGE_NOT_FOUND(BAD_REQUEST,"D003","거래글 이미지를 찾을 수 없습니다."),
    BUYER_NOT_FOUND(BAD_REQUEST,"D004","구매자를 찾을 수 없습니다."),
    SAME_BUYER_SELLER(BAD_REQUEST,"D005","판매자와 구매자가 같습니다."),
    DEAL_REVIEW_NOT_FOUND(BAD_REQUEST,"D006","거래 리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_USER_TO_DEAL_REVIEW(FORBIDDEN,"D007","거래 리뷰에대한 권한이 없습니다."),
    DEAL_SUCCESS_NOT_FOUND(BAD_REQUEST,"D008","거래 완료되지 않은 글입니다."),
    DEALPOST_NOT_DONE(BAD_REQUEST,"D009","거래글이 완료 상태가 아닙니다."),
    DEALPOST_USER_NOT_FOUND(BAD_REQUEST,"D010","거래글 작성자가 존재하지 않습니다."),
    DEALPOST_STATE_SAME(BAD_REQUEST,"D011","거래글 상태가 이전과 동일합니다."),
    UNAUTHORIZED_USER_TO_MANNER_REVIEW(BAD_REQUEST,"D012","매너 리뷰에대한 권한이 없습니다."),

    // Common
    INVALID_TYPE_VALUE(BAD_REQUEST, "C001", "Invalid Type Value"),
    INVALID_INPUT_VALUE(BAD_REQUEST,"C002","Invalid Input Value"),
    NOTHING_HAPPEN_BECAUSE_EMPTY(BAD_REQUEST,"C003","비어있어 처리할것이 없습니다."),
    SESSION_NULL(BAD_REQUEST,"C004","session 이 null 입니다."),
    SESSION_ALREADY_INVALIDATED(BAD_REQUEST,"C005","session 이 이미 비활성화 되었습니다."),

    // Server
    UNDEFINED_ERROR(INTERNAL_SERVER_ERROR, "S001", "정의되지 않은 에러입니다."),
    FILE_NOT_FOUND(INTERNAL_SERVER_ERROR,"S002","파일을 읽을 수 없습니다."),
    FILE_DELETE_FAIL(INTERNAL_SERVER_ERROR,"S003","파일을 삭제할 수 없습니다."),
    MEDIA_TYPE_NOT_FOUND(INTERNAL_SERVER_ERROR,"S004","미디어 파일 타입이 유효하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
