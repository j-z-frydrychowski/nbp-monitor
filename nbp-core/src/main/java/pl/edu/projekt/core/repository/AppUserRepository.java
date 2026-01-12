package pl.edu.projekt.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.projekt.core.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
