// src/test/java/edu/sjsu/library/UserDaoIT.java
package edu.sjsu.library;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import edu.sjsu.library.models.User.UserRole;
import edu.sjsu.library.models.User.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = App.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // use your Postgres
class UserDaoIT {

  @Autowired UserDAO dao;
  @Autowired JdbcTemplate jdbc;

  @BeforeEach
  void setup() {
    dao.createTable(); // idempotent
    jdbc.update("TRUNCATE TABLE users RESTART IDENTITY"); // clean slate for each test
  }

  @Test
  void insert_and_findByEmail_then_findById() {
    // unique email per test run to avoid duplicates
    String email = "john+" + UUID.randomUUID() + "@example.com";

    var u = new User("John", "Doe", email, "hash", UserRole.MEMBER);
    int rows = dao.insert(u);                      // returns update count (1)
    assertEquals(1, rows);

    var gotByEmail = dao.findByEmail(email);       // fetch without needing the id
    assertNotNull(gotByEmail);
    assertEquals(email, gotByEmail.getEmail());
    assertEquals(UserRole.MEMBER, gotByEmail.getRole());
    assertEquals(UserStatus.ACTIVE, gotByEmail.getStatus());

    // now that we have the id from DB, we can verify findById too
    var gotById = dao.findById(gotByEmail.getUserID());
    assertNotNull(gotById);
    assertEquals(email, gotById.getEmail());
  }

  @Test
  void findAll_returns_list() {
    // ensure at least one row exists
    var u = new User("Jane", "Doe", "jane+" + UUID.randomUUID() + "@example.com",
                     "hash2", UserRole.STAFF);
    assertEquals(1, dao.insert(u));

    var list = dao.findAll();
    assertNotNull(list);
    assertFalse(list.isEmpty());
  }
}
