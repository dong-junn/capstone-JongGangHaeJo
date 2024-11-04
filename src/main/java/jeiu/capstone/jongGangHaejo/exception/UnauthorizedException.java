package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final CommonErrorCode errorCode;

    public UnauthorizedException(String message, CommonErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
