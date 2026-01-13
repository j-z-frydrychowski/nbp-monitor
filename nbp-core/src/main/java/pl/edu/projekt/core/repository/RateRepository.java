package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.Currency;
import pl.edu.projekt.core.entity.Rate;

import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findTopByCurrencyOrderByDateDesc(Currency currency);
}
