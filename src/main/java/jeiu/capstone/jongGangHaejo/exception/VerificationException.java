package jeiu.capstone.jongGangHaejo.exception;

import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;

public class VerificationException extends FileServiceException {
    public VerificationException(String message) {
        super(message, CommonErrorCode.VERIFICATION_ERROR);
    }
} 