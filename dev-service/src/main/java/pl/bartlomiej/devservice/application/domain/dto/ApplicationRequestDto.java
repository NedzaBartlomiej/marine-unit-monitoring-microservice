package pl.bartlomiej.devservice.application.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplicationRequestDto(@NotBlank(message = "EMPTY_NAME") String name,
                                    @NotBlank(message = "EMPTY_REQUEST_DESCRIPTION") String requestDesc) {
}