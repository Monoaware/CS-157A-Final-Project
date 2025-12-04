package edu.sjsu.library.utils;

import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

// @Configuration - COMMENTED OUT to prevent database reset on every startup
// Uncomment @Configuration and restart the server ONCE if you need to reset the database
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // Run schema SQL
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/create_schema.sql"));

            // Run data initialization SQL
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/initialize_data.sql"));

            System.out.println("Database initialized successfully!");
        }
    }
}