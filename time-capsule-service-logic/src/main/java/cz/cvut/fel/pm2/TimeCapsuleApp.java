package cz.cvut.fel.pm2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Time Capsule application.
 * This class is responsible for bootstrapping the Spring Boot application.
 * It also enables various Spring features such as caching, Kafka, JPA repositories,
 * transaction management, and scheduling.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableCaching
@EnableKafka
@EnableJpaRepositories
@EnableTransactionManagement
@EnableScheduling
public class TimeCapsuleApp {
    /**
     * The main method that serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TimeCapsuleApp.class, args);
    }
}