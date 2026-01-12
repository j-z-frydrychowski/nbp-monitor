package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.FetchLog;

public interface FetchLogRepository extends JpaRepository<FetchLog,Long> {
}
