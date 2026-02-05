package io.github.yuyeol3.yachtbackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("/api/users")
@RequiredArgsConstructor
public class UserController {

    @PostMapping
    public ResponseEntity<?> create() {

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get() {

    }


    @DeleteMapping("/me")
    public  ResponseEntity<?> delete() {

    }

}
