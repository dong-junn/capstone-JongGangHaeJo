package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;

/**
 * 잘못된 파일 이름 예외
 */
public class InvalidFileNameException extends FileServiceException {
    public InvalidFileNameException(String message) {
        super(message, CommonErrorCode.INVALID_ARGUMENT_ERROR);
    }

    public InvalidFileNameException(String message, Throwable cause) {
        super(message, cause, CommonErrorCode.INVALID_ARGUMENT_ERROR);
    }
}
