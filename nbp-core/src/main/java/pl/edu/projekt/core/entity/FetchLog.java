package pl.edu.projekt.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class FetchLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp; // Kiedy pobrano dane
    private String status;           // SUCCESS lub ERROR
    private String message;          // Krótki opis (np. "Pobrano 34 waluty")
}