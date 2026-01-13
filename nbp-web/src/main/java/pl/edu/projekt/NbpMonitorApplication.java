package pl.edu.projekt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.edu.projekt.core.entity.AppUser;
import pl.edu.projekt.core.repository.AppUserRepository;

@SpringBootApplication
@EnableScheduling
public class NbpMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NbpMonitorApplication.class, args);
    }

    @Bean
    public CommandLineRunner createTestUser(AppUserRepository appUserRepository) {
        return args -> {
            if (appUserRepository.count() == 0) {
                AppUser user = new AppUser();
                user.setName("Jan Testowy");
                user.setEmail("jan@test.pl");

                appUserRepository.save(user);
                System.out.println(">>> (FIX) Utworzono użytkownika testowego: " + user.getName());
            } else {
                System.out.println(">>> (FIX) Użytkownik testowy już istnieje.");
            }
        };
    }
}