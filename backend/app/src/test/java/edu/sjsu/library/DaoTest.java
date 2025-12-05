package edu.sjsu.library;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.dao.TitleDAO;
import edu.sjsu.library.dao.CopyDAO;
import edu.sjsu.library.dao.BookRecordDAO;
import edu.sjsu.library.dao.FineDAO;
import edu.sjsu.library.dao.HoldDAO;

import edu.sjsu.library.models.User;
import edu.sjsu.library.models.User.UserRole;
import edu.sjsu.library.models.User.UserStatus;
import edu.sjsu.library.models.Title;
import edu.sjsu.library.models.Title.Genre;
import edu.sjsu.library.models.Copy;
import edu.sjsu.library.models.Copy.CopyStatus;
import edu.sjsu.library.models.BookRecord;
import edu.sjsu.library.models.Fine;
import edu.sjsu.library.models.Hold;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = App.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // use Postgres
class DaoIntegrationIT {

  @Autowired UserDAO userDao;
  @Autowired TitleDAO titleDao;
  @Autowired CopyDAO copyDao;
  @Autowired BookRecordDAO bookRecordDao;
  @Autowired FineDAO fineDao;
  @Autowired HoldDAO holdDao;

  @Autowired JdbcTemplate jdbc;

  @BeforeEach
  void setup() {
    // Ensure tables exist (assuming each DAO has an idempotent createTable())
    userDao.createTable();
    titleDao.createTable();
    copyDao.createTable();
    bookRecordDao.createTable();
    fineDao.createTable();
    holdDao.createTable();

    // TRUNCATE in FK-safe order (children first, then parents).
    jdbc.update("""
      TRUNCATE TABLE fines, holds, book_records, copies, titles, users
      RESTART IDENTITY CASCADE
    """);
  }

  // ---------------------------------------------------------------------------
  // USER DAO TESTS
  // ---------------------------------------------------------------------------

  @Test
  void userDao_insert_and_findByEmail_then_findById() {
    // unique email per test run to avoid duplicates
    String email = "john+" + UUID.randomUUID() + "@example.com";

    var u = new User("John", "Doe", email, "hash", UserRole.MEMBER);
    int rows = userDao.insert(u);
    assertEquals(1, rows);

    var gotByEmail = userDao.findByEmail(email);
    assertNotNull(gotByEmail);
    assertEquals(email, gotByEmail.getEmail());
    assertEquals(UserRole.MEMBER, gotByEmail.getRole());
    assertEquals(UserStatus.ACTIVE, gotByEmail.getStatus());

    var gotById = userDao.findById(gotByEmail.getUserID());
    assertNotNull(gotById);
    assertEquals(email, gotById.getEmail());
  }

  @Test
  void userDao_findAll_returns_list() {
    var u = new User(
        "Jane",
        "Doe",
        "jane+" + UUID.randomUUID() + "@example.com",
        "hash2",
        UserRole.STAFF
    );
    assertEquals(1, userDao.insert(u));

    var list = userDao.findAll();
    assertNotNull(list);
    assertFalse(list.isEmpty());
  }

  // ---------------------------------------------------------------------------
  // TITLE DAO TESTS
  // ---------------------------------------------------------------------------

  @Test
  void titleDao_insert_and_findAll() {
    Title t = new Title(
        "978-3-" + UUID.randomUUID(), 
        "Test Book",
        "Test Author", 
        Year.of(2024),
        Genre.FICTION
    );

    int rows = titleDao.insert(t);
    assertEquals(1, rows);

    List<Title> titles = titleDao.findAll();
    assertNotNull(titles);
    assertFalse(titles.isEmpty());

    boolean found = titles.stream().anyMatch(x -> "Test Book".equals(x.getTitle()));
    assertTrue(found, "Inserted title should be present in findAll()");
  }

  // ---------------------------------------------------------------------------
  // COPY DAO TESTS
  // ---------------------------------------------------------------------------

  @Test
  void copyDao_insert_and_findAll() {
    // First insert a Title to satisfy FK constraint
    Title t = new Title(
        "978-1-" + UUID.randomUUID(),
        "Copy Test Book",
        "Copy Author",
        Year.of(2023),
        Genre.FICTION
    );
    assertEquals(1, titleDao.insert(t));
    Title persistedTitle = titleDao.findAll().get(0);

    Copy c = new Copy(
        persistedTitle.getTitleID(),
        "BC-" + UUID.randomUUID(),
        CopyStatus.AVAILABLE,
        "Main Branch"
    );

    int rows = copyDao.insert(c);
    assertEquals(1, rows);

    List<Copy> copies = copyDao.findAll();
    assertNotNull(copies);
    assertFalse(copies.isEmpty());
  }

  // ---------------------------------------------------------------------------
  // BOOK RECORD DAO TESTS
  // ---------------------------------------------------------------------------

  @Test
  void bookRecordDao_insert_and_findAll() {
    // Need a user and a copy first to satisfy FKs
    String email = "loanuser+" + UUID.randomUUID() + "@example.com";
    User user = new User("Loan", "User", email, "hash3", UserRole.MEMBER);
    assertEquals(1, userDao.insert(user));
    User persistedUser = userDao.findByEmail(email);

    Title t = new Title(
        "978-0-" + UUID.randomUUID(),
        "Loan Book",
        "Loan Author",
        Year.of(2022),
        Genre.FICTION
    );
    assertEquals(1, titleDao.insert(t));
    Title persistedTitle = titleDao.findAll().get(0);

    Copy c = new Copy(
        persistedTitle.getTitleID(),
        "BC-LOAN-" + UUID.randomUUID(),
        CopyStatus.AVAILABLE,
        "Main Branch"
    );
    assertEquals(1, copyDao.insert(c));
    Copy persistedCopy = copyDao.findAll().get(0);

    BookRecord br = new BookRecord(
        persistedCopy.getCopyID(),
        persistedUser.getUserID()
    );

    int rows = bookRecordDao.insert(br);
    assertEquals(1, rows);

    List<BookRecord> records = bookRecordDao.findAll();
    assertNotNull(records);
    assertFalse(records.isEmpty());
  }

  // ---------------------------------------------------------------------------
  // FINE DAO TESTS
  // ---------------------------------------------------------------------------

  @Test
  void fineDao_insert_and_findAll() {
    // Pre-req: user + book record
    String email = "fineuser+" + UUID.randomUUID() + "@example.com";
    User user = new User("Fine", "User", email, "hash4", UserRole.MEMBER);
    assertEquals(1, userDao.insert(user));
    User persistedUser = userDao.findByEmail(email);

    Title t = new Title(
        "978-9-" + UUID.randomUUID(),
        "Fine Book",
        "Fine Author",
        Year.of(2021),
        Genre.HISTORY
    );
    assertEquals(1, titleDao.insert(t));
    Title persistedTitle = titleDao.findAll().get(0);

    Copy c = new Copy(
        persistedTitle.getTitleID(),
        "BC-FINE-" + UUID.randomUUID(),
        CopyStatus.AVAILABLE,
        "Main Branch"
    );
    assertEquals(1, copyDao.insert(c));
    Copy persistedCopy = copyDao.findAll().get(0);

    BookRecord br = new BookRecord(
        persistedCopy.getCopyID(),
        persistedUser.getUserID()
    );
    assertEquals(1, bookRecordDao.insert(br));
    BookRecord persistedRecord = bookRecordDao.findAll().get(0);

    Fine fine = new Fine(
        persistedUser.getUserID(),
        persistedRecord.getLoanID(),
        new BigDecimal("5.00"),
        "Overdue"
    );

    int rows = fineDao.insert(fine);
    assertEquals(1, rows);

    List<Fine> fines = fineDao.findAll();
    assertNotNull(fines);
    assertFalse(fines.isEmpty());
  }

  // ---------------------------------------------------------------------------
  // HOLD DAO TESTS
  // ---------------------------------------------------------------------------

  @Test
  void holdDao_insert_and_findAll() {
    // Pre-req: user + title (+ optional copy)
    String email = "holduser+" + UUID.randomUUID() + "@example.com";
    User user = new User("Hold", "User", email, "hash5", UserRole.MEMBER);
    assertEquals(1, userDao.insert(user));
    User persistedUser = userDao.findByEmail(email);

    Title t = new Title(
        "978-7-" + UUID.randomUUID(),
        "Hold Book",
        "Hold Author",
        Year.of(2020),
        Genre.FICTION
    );
    assertEquals(1, titleDao.insert(t));
    Title persistedTitle = titleDao.findAll().get(0);

    Copy c = new Copy(
        persistedTitle.getTitleID(),
        "BC-HOLD-" + UUID.randomUUID(),
        CopyStatus.CHECKED_OUT,
        "Main Branch"
    );
    assertEquals(1, copyDao.insert(c));
    Copy persistedCopy = copyDao.findAll().get(0);

    Hold hold = new Hold(
        persistedUser.getUserID(),
        persistedTitle.getTitleID(),
        persistedCopy.getCopyID(),
        1
    );

    int rows = holdDao.insert(hold);
    assertEquals(1, rows);

    List<Hold> holds = holdDao.findAll();
    assertNotNull(holds);
    assertFalse(holds.isEmpty());
  }
}
