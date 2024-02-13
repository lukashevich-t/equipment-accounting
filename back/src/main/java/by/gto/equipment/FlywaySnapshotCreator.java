package by.gto.equipment;

import java.io.InputStream;
import java.util.Properties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.BaselineResult;

public class FlywaySnapshotCreator {
//    public static void flywayDiff() {
//        Properties p = new Properties();
//        try (InputStream is = FlywaySnapshotCreator.class.getClassLoader().getResourceAsStream("application.properties")) {
//            p.load(is);
//            String schemas = p.getProperty("spring.flyway.schemas");
//
//            Flyway flyway = Flyway.configure()
//                    .dataSource(p.getProperty("spring.datasource.url"), p.getProperty("spring.datasource.username"), p.getProperty("spring.datasource.password"))
//                    .locations(p.getProperty("spring.flyway.locations", "classpath:db/migration"))
//                    .schemas(schemas.split(","))
//                    .load();
//            BaselineResult baseline = flyway.baseline();
//            System.out.println(baseline);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static void printVars() {
        Properties p = new Properties();
        try (InputStream is = FlywaySnapshotCreator.class.getClassLoader().getResourceAsStream("application.properties")) {
            p.load(is);
            String dbUser = p.getProperty("spring.datasource.username");
            String dbPassword = p.getProperty("spring.datasource.password");
            System.err.println(dbUser + " " + dbPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        printVars();
    }
}
