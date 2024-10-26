package jeiu.capstone.jongGangHaejo.exception;

/**
 * 파일 서비스 관련 예외의 공통 기반 클래스
 */
public class FileServiceException extends RuntimeException {
    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}