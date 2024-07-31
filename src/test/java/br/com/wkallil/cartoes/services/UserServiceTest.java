package br.com.wkallil.cartoes.services;

import br.com.wkallil.cartoes.dtos.UserCreateDto;
import br.com.wkallil.cartoes.models.UserModel;
import br.com.wkallil.cartoes.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUserSuccess() {
        UserCreateDto userCreateDto = new UserCreateDto("John Doe", "johndoe",  "123456", "johndoe@example.com", "12345-678");
        UserModel userModel = new UserModel();
        userModel.setName(userCreateDto.name());
        userModel.setUsername(userCreateDto.username());
        userModel.setEmail(userCreateDto.email());
        userModel.setCep(userCreateDto.cep());
        userModel.setPassword("encodedPassword");

        when(passwordEncoder.encode(userCreateDto.password())).thenReturn("encodedPassword");
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        UserModel result = userService.save(userCreateDto);

        assertEquals("John Doe", result.getName());
        assertEquals("johndoe", result.getUsername());
        assertEquals("johndoe@example.com", result.getEmail());
        assertEquals("12345-678", result.getCep());
        assertEquals("encodedPassword", result.getPassword());

        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSaveUserDuplicateUsernameOrEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("John Doe", "johndoe", "johndoe@example.com", "123456", "12345-678");

        when(passwordEncoder.encode(userCreateDto.password())).thenReturn("encodedPassword");
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));
        when(userRepository.save(any(UserModel.class))).thenThrow(new DataIntegrityViolationException("Username or email already in use."));

        assertThrows(IllegalArgumentException.class, () -> userService.save(userCreateDto));
    }

    @Test
    void testGetByUsernameSuccess() {
        UserModel userModel = new UserModel();
        userModel.setUsername("johndoe");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(userModel));

        UserModel result = userService.getByUsername("johndoe");

        assertEquals("johndoe", result.getUsername());
    }

    @Test
    void testGetByUsernameNotFound() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getByUsername("johndoe"));
    }

    @Test
    void testUserInfoSuccess() {
        UUID userId = UUID.randomUUID();
        UserModel userModel = new UserModel();
        userModel.setUserID(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        UserModel result = userService.userInfo(userId);

        assertEquals(userId, result.getUserID());
    }

    @Test
    void testUserInfoNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.userInfo(userId));
    }
}
