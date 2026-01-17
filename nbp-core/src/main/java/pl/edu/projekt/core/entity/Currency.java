package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Kod waluty (np. USD) musi być unikalny
    private String code;

    private String name;

    // Relacja: Jedna waluta ma wiele notowań (kursów)
    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Rate> rates;

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