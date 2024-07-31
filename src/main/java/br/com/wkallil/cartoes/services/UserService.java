package br.com.wkallil.cartoes.services;

import br.com.wkallil.cartoes.dtos.UserCreateDto;

import br.com.wkallil.cartoes.models.UserModel;

import br.com.wkallil.cartoes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    @Transactional
    public UserModel save(UserCreateDto userCreateDto) {
        UserModel user = new UserModel();
        user.setName(userCreateDto.name());
        user.setUsername(userCreateDto.username());
        user.setEmail(userCreateDto.email());
        user.setCep(userCreateDto.cep());
        user.setPassword(passwordEncoder.encode(userCreateDto.password()));
        sendEmailVerification(user);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username or email already in use.");
        }
    }

    @Transactional
    public UserModel getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }


    @Transactional
    public UserModel userInfo(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }


    private void sendEmailVerification(UserModel user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Email de registro de conta");
        email.setText("Email de confirmação de registro. Obrigado, " + user.getName() +
                "! Estamos felizes pelo seu cadastro.");
        emailSender.send(email);

    }

}
