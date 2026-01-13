package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.Currency;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCode(String code);
    List<Currency> findByCodeIn(Set<String> codes);
}