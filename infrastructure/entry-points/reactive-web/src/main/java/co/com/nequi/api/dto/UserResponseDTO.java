package co.com.nequi.api.dto;

public record UserResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String avatar
) {
}
