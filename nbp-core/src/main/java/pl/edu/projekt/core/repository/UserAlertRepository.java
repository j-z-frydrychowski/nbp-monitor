package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.UserAlert;

public interface UserAlertRepository extends JpaRepository<UserAlert, Long> {
}
