package jeiu.capstone.jongGangHaejo.exception;

/**
 * AWS SDK 관련 예외
 */
public class AwsSdkException extends FileServiceException {
    public AwsSdkException(String message, Throwable cause) {
        super(message, cause);
    }
}