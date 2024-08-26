package by.gto.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EquipmentAccountingApplicationTests {
@Container
	private MariaDBContainer<?> mariaDb = new MariaDBContainer<>("mariadb:10.6");

	@Test
	void contextLoads() {
	}

}
