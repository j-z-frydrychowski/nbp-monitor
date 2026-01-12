package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Kod waluty (np. USD) musi być unikalny
    private String code;

    private String name;

    // Relacja: Jedna waluta ma wiele notowań (kursów)
    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private List<Rate> rates;
}