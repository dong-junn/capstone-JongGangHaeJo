package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;

/**
 * S3 업로드 관련 예외
 */
public class S3UploadException extends FileServiceException {
    public S3UploadException(String message) {
        super(message, CommonErrorCode.FILE_UPLOAD_ERROR);
    }

    public S3UploadException(String message, Throwable cause) {
        super(message, cause, CommonErrorCode.FILE_UPLOAD_ERROR);
    }
}
