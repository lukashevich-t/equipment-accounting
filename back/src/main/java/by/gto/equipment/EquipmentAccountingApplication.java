package by.gto.equipment;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EquipmentAccountingApplication {
    // @PostConstruct
    // public void init() {
    //     System.out.println("111");
    // }

    public static void main(String[] args) {
        SpringApplication.run(EquipmentAccountingApplication.class, args);
    }
}
