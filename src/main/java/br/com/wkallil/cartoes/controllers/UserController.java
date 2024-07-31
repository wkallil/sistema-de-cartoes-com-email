package br.com.wkallil.cartoes.controllers;

import br.com.wkallil.cartoes.dtos.UserCreateDto;
import br.com.wkallil.cartoes.models.UserModel;
import br.com.wkallil.cartoes.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@RequestBody UserCreateDto user) {
        UserModel savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserModel> getUserInfo(@PathVariable("userId") UUID userId) {
        UserModel userInfo = userService.userInfo(userId);
        return ResponseEntity.ok(userInfo);
    }



}
