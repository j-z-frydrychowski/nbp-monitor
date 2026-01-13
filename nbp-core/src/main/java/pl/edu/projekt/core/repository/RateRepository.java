package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.projekt.core.entity.Currency;
import pl.edu.projekt.core.entity.Rate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RateRepository extends JpaRepository<Rate, Long> {

    @Query("SELECT r FROM Rate r WHERE r.date = (SELECT MAX(r2.date) FROM Rate r2 WHERE r2.currency.id = r.currency.id) AND r.currency.code IN :currencyCodes")
    List<Rate> findLatestRatesForCurrencies(@Param("currencyCodes") Set<String> currencyCodes);

}
