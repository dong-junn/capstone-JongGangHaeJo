package jeiu.capstone.jongGangHaejo.exception;

/**
 * 파일 서비스 관련 예외의 기본 클래스
 */
public class FileServiceException extends RuntimeException {
    private final ErrorCode errorCode;

    public FileServiceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileServiceException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
