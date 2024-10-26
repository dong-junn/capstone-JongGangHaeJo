package jeiu.capstone.jongGangHaejo.exception;

/**
 * S3에 파일 업로드 중 발생하는 예외
 */
public class S3UploadException extends FileServiceException {
    public S3UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}