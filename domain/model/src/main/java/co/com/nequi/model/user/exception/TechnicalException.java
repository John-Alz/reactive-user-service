package co.com.nequi.model.user.exception;

import co.com.nequi.model.user.enums.TechnicalMessage;

public class TechnicalException extends ProcessorException {

    public TechnicalException(TechnicalMessage message) {
        super(message.getMessage(), message);
    }

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }
}
