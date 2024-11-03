package jeiu.capstone.jongGangHaejo.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
