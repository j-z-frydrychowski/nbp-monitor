package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currencyCode; // Kod waluty, której dotyczy alert (np. USD)
    private Double threshold;    // Próg kwotowy

    // Relacja: Alert należy do jednego użytkownika
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
}