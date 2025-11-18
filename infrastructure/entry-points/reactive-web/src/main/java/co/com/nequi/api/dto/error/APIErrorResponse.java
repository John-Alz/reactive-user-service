package co.com.nequi.api.dto.error;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder(toBuilder = true)
public class APIErrorResponse {

    String messageId;

    Integer status;

    String title;

    List<ErrorDTO> errors;

}
