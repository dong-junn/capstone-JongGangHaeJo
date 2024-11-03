package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;

/**
 * 일반적인 파일 업로드 예외
 */
public class FileUploadException extends FileServiceException {
    public FileUploadException(String message) {
        super(message, CommonErrorCode.FILE_UPLOAD_ERROR);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause, CommonErrorCode.FILE_UPLOAD_ERROR);
    }
}
