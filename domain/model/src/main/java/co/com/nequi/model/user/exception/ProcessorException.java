package co.com.nequi.model.user.exception;

import co.com.nequi.model.user.enums.TechnicalMessage;

public class ProcessorException extends RuntimeException{

    private final TechnicalMessage technicalMessage;

    public  ProcessorException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    public ProcessorException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause);
        this.technicalMessage = technicalMessage;
    }


}
