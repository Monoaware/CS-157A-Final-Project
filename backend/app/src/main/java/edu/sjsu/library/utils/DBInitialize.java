package edu.sjsu.library.utils;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.dao.TitleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBInitialize {

    private static final Logger log = LoggerFactory.getLogger(DBInitialize.class);

    @Bean
    CommandLineRunner initTables(UserDAO userDAO, TitleDAO titleDAO) {
        return args -> {
            log.info("Initializing database tables...");
            userDAO.createTable();
            titleDAO.createTable();
            log.info("Database tables initialized.");
        };
    }
}