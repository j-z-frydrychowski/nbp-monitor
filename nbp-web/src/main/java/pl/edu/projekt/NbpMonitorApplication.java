package pl.edu.projekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NbpMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(NbpMonitorApplication.class, args);
    }
}