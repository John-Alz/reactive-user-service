package co.com.nequi.model.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501","Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    INVALID_MESSAGE_ID("404", "Invalid Message ID, please verify", "messageId"),
    UNSUPPORTED_OPERATION("501", "Method not supported, please try again", ""),
    USER_NOT_FOUND("404", "The user is not exists.", ""),
    EXTERNAL_SERVICE_ERROR("400", "Something went wrong, please try again later.", ""),
    DATABASE_ERROR("500", "We're sorry, there was an internal error processing your request.", "");


    private final String code;
    private final String message;
    private final String param;

}
