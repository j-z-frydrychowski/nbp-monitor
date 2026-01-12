package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.Rate;

public interface RateRepository extends JpaRepository<Rate, Long> {
}
