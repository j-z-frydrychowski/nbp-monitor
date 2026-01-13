package pl.edu.projekt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import pl.edu.projekt.core.entity.AppUser;
import pl.edu.projekt.core.repository.AppUserRepository;

@SpringBootApplication
@EnableScheduling
public class NbpMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NbpMonitorApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner createTestUser(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (appUserRepository.count() == 0) {
                AppUser user = new AppUser();
                user.setName("Jan Testowy");
                user.setEmail("jan@test.pl");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole("USER");

                appUserRepository.save(user);
                System.out.println(">>> (SECURITY) Utworzono użytkownika: jan@test.pl / hasło: user123");
            }
        };
    }
}