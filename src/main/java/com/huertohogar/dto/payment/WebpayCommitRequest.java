package com.huertohogar.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WebpayCommitRequest {
    @NotBlank(message = "token requerido")
    private String token;
}