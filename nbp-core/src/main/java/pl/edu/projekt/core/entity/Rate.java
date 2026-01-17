package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double mid; // Kurs średni
    private LocalDate date; // Data notowania

    // Relacja: Wiele kursów należy do jednej waluty
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        return id != null && id.equals(((Currency) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}