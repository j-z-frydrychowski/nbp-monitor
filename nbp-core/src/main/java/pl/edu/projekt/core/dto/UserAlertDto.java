package pl.edu.projekt.core.dto;

import lombok.Data;

@Data
public class UserAlertDto {
    private Long userId;
    private String currencyCode;
    private Double threshold;
}
