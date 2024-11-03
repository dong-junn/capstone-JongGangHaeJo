package jeiu.capstone.jongGangHaejo.exception;

import org.springframework.http.HttpStatus;

/**
 * 오류 코드를 정의하기 위한 인터페이스
 */
public interface ErrorCode {
    HttpStatus getHttpStatus();
    int getCode();
    String getMessage();
}
