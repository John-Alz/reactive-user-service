package co.com.nequi.sqs.listener.dto;

public record UserEventDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String avatar
) {

}
