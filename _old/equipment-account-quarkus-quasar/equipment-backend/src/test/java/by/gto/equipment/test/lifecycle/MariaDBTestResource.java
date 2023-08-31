package by.gto.equipment.test.lifecycle;

import static by.gto.equipment.test.helpers.LogHelpersKt.writeObjectToFile;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MariaDBContainer;

public class MariaDBTestResource implements QuarkusTestResourceLifecycleManager {
    static MariaDBContainer<?> db;
    final String LOG_FILE_NAME = "build/MariaDBTestResource.log";
    final String DB_NAME = "equipment";
    final String DB_USER = "equipment_res";
    final String DB_PASS = "equipment_res";

    @Override
    public Map<String, String> start() {
        try {
            StringBuilder sb = new StringBuilder("MariaDBTestResource.start ").append(new Date());
            writeObjectToFile(sb.append("\n\n"), LOG_FILE_NAME, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db = new MariaDBContainer<>("mariadb:10.7.4")
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USER)
            .withPassword(DB_PASS);
        db.start();

        final Map<String, String> conf = new HashMap<>();
        conf.put("%test.quarkus.datasource.jdbc.url", db.getJdbcUrl());
        conf.put("%test.quarkus.datasource.username", DB_USER);
        conf.put("%test.quarkus.datasource.password", DB_PASS);
        conf.put("%test.quarkus.datasource.db-kind", "mariadb");

        return conf;
    }

    @Override
    public void stop() {
        if (db != null) {
            getResultingDatabaseContent();
            db.stop();
        }
    }

    private void getResultingDatabaseContent() {
        try {
            // final Container.ExecResult execResult = db.execInContainer("mysql", "-uroot", "-p" + DB_PASS, "-e", "show databases;");
            final Container.ExecResult execResult = db.execInContainer(
                "mysqldump",
                "-uroot",
                "-p" + DB_PASS,
                "--add-drop-database",
                "--hex-blob",
                "--default_character_set=utf8",
                "-Q",
                "-R",
                "--triggers",
                "-B",
                DB_NAME
            );
            writeObjectToFile(execResult.getStdout(), "build/mariadb-after-tests.sql", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
