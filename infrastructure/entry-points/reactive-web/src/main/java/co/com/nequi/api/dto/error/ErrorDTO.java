package co.com.nequi.api.dto.error;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ErrorDTO {

    String code;

    String message;

}
