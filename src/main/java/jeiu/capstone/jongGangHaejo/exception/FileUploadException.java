package jeiu.capstone.jongGangHaejo.exception;

/**
 * 파일 업로드 중 알 수 없는 예외
 */
public class FileUploadException extends FileServiceException {
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}