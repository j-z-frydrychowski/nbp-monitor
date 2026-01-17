package pl.edu.projekt.web.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.projekt.core.entity.AppUser;
import pl.edu.projekt.core.repository.AppUserRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder; // Wymaga beana z SecurityConfig

    @Override
    public void run(String... args) throws Exception {
        if (appUserRepository.count() == 0) {

            AppUser user = new AppUser();
            user.setName("Jan Testowy");
            user.setEmail("jan@test.pl");
            user.setPassword(passwordEncoder.encode("user123"));

            user.setRole("USER");

            appUserRepository.save(user);
            System.out.println(">>> UTWORZONO UŻYTKOWNIKA STARTOWEGO: " + user.getEmail());
        }
    }
}