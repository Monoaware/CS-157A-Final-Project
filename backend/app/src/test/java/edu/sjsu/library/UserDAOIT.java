// src/test/java/edu/sjsu/library/UserDaoIT.java
package edu.sjsu.library;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDaoIT {

  @Autowired UserDAO dao;

  @BeforeEach
  void setup() {
    dao.createTable();
    // clean table if desired
    // dao.delete(1); ...
  }

  @Test
  void insert_and_findById() {
    var u = new User(0, "John", "Doe", "john@example.com", "hash", "USER");
    int id = dao.insert(u);     
    var got = dao.findById(id);             
    assertNotNull(got);
    assertEquals("john@example.com", got.getEmail());
  }

  @Test
  void findAll_returns_list() {
    var list = dao.findAll();
    assertNotNull(list);
  }
}
