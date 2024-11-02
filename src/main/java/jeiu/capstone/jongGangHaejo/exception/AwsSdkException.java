package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;

/**
 * AWS SDK 관련 예외
 */
public class AwsSdkException extends FileServiceException {
    public AwsSdkException(String message) {
        super(message, CommonErrorCode.FILE_UPLOAD_ERROR);
    }

    public AwsSdkException(String message, Throwable cause) {
        super(message, cause, CommonErrorCode.FILE_UPLOAD_ERROR);
    }
}
