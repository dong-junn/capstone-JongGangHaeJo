package jeiu.capstone.jongGangHaejo.exception;

/**
 * 파일 이름이 유효하지 않을 때 발생하는 예외
 */
public class InvalidFileNameException extends FileServiceException {
    public InvalidFileNameException(String message) {
        super(message);
    }
}