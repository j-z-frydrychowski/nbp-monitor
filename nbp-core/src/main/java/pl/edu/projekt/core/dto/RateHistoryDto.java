package pl.edu.projekt.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RateHistoryDto {
    private LocalDate date;
    private Double mid;
}
