package jeiu.capstone.jongGangHaejo.exception.common;

import jeiu.capstone.jongGangHaejo.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 공통 오류 코드를 정의하는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    // 4xx Client Errors
    INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, 4001, "올바르지 않은 파라미터입니다."),
    INVALID_FORMAT_ERROR(HttpStatus.BAD_REQUEST, 4002, "올바르지 않은 포맷입니다."),
    INVALID_TYPE_ERROR(HttpStatus.BAD_REQUEST, 4003, "올바르지 않은 타입입니다."),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, 4004, "필수 파라미터가 없습니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED,4010, "로그인 되지 않았습니다. 로그인 후 이용해주세요"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, 4011, "인증에 실패했습니다."),
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, 4031, "권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, 4041, "요청한 게시물을 찾을 수 없습니다. 게시물 번호: {0}"),
    LOGIN_ID_NOT_FOUND(HttpStatus.UNAUTHORIZED, 4012, "존재하지 않는 아이디입니다."),
    LOGIN_PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, 4013, "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 4014, "유효하지 않은 토큰입니다."),

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5001, "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "파일 업로드 중 오류가 발생했습니다."),
    FILE_DOWNLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5003, "파일 다운로드 중 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5004, "데이터베이스 오류가 발생했습니다."),
    THIRD_PARTY_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5005, "외부 서비스와의 통신 오류가 발생했습니다."),

    //Admin 관련에러
    ADMIN_USERNAME_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, 6000, "존재하지 않는 회원입니다"),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6100, "이메일 발송 중 오류가 발생했습니다."),
    VERIFICATION_ERROR(HttpStatus.BAD_REQUEST, 6101, "이메일 인증 처리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
