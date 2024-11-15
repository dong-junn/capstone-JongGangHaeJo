package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;

public class EmailSendException extends FileServiceException {
    public EmailSendException(String message) {
        super(message, CommonErrorCode.EMAIL_SEND_ERROR);
    }
} 