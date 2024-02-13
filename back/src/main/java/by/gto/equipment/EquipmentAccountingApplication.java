package by.gto.equipment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Стартовый класс для приложения.
 */
@SpringBootApplication
public class EquipmentAccountingApplication {
    // @PostConstruct
    // public void init() {
    //     System.out.println("111");
    // }

    @Value("spring.datasource.username")
    private String dbUser;

    @Value("spring.datasource.password")
    private String dbPassword;

    public static void main(String[] args) {
        SpringApplication.run(EquipmentAccountingApplication.class, args);
    }
}
