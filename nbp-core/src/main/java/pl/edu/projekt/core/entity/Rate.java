package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double mid; // Kurs średni
    private LocalDate date; // Data notowania

    // Relacja: Wiele kursów należy do jednej waluty
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;
}