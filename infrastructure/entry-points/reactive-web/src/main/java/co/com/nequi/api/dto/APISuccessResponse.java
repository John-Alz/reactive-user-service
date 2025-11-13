package co.com.nequi.api.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class APISuccessResponse<T> {
    private String messageId;
    private T data;
}
