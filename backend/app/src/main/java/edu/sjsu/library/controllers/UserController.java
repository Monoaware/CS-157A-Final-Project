package edu.sjsu.library.controllers;

import edu.sjsu.library.dao.UserDAO;
import edu.sjsu.library.models.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserDAO dao;

    public UserController(UserDAO dao) {
        this.dao = dao;
    }

    /** GET /users */
    @GetMapping
    public List<User> list() {
        return dao.findAll();
    }

    /** GET /users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<User> one(@PathVariable int id) {
        var u = dao.findById(id);
        return (u == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(u);
    }

    /** POST /users  (returns 201 + Location + body) */
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserReq req) {
        var u = new User();
        u.setFname(req.fname());
        u.setLname(req.lname());
        u.setEmail(req.email());
        u.setPasswordhash(req.passwordhash());
        u.setRole(req.role() == null ? "USER" : req.role());

        int newId = dao.insert(u);        // sets u.id inside your DAO as well
        u.setId(newId);

        return ResponseEntity
                .created(URI.create("/users/" + newId))
                .body(u);
    }

    /** PUT /users/{id}  (full update) */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @Valid @RequestBody UpdateUserReq req) {
        var existing = dao.findById(id);
        if (existing == null) throw new NotFound(id);

        existing.setFname(req.fname());
        existing.setLname(req.lname());
        existing.setEmail(req.email());
        existing.setPasswordhash(req.passwordhash());
        existing.setRole(req.role());

        dao.update(existing);
    }

    /** DELETE /users/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        var existing = dao.findById(id);
        if (existing == null) throw new NotFound(id);
        dao.delete(id);
    }

    /* -------- DTOs & Errors -------- */

    public record CreateUserReq(
            @NotBlank String fname,
            @NotBlank String lname,
            @Email @NotBlank String email,
            @NotBlank String passwordhash,
            @Pattern(regexp="ADMIN|USER", message="role must be ADMIN or USER")
            String role
    ) {}

    public record UpdateUserReq(
            @NotBlank String fname,
            @NotBlank String lname,
            @Email @NotBlank String email,
            @NotBlank String passwordhash,
            @Pattern(regexp="ADMIN|USER", message="role must be ADMIN or USER")
            String role
    ) {}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class NotFound extends RuntimeException {
        NotFound(int id){ super("user " + id + " not found"); }
    }
}
