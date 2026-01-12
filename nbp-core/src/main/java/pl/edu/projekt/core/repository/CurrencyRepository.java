package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.Currency;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    // Metoda pomocnicza, którą Spring sam zaimplementuje na podstawie nazwy!
    Optional<Currency> findByCode(String code);
}