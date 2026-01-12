package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // Relacja: Użytkownik może mieć wiele alertów
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAlert> alerts;
}