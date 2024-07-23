package br.com.wkallil.cartoes.services;

import br.com.wkallil.cartoes.dtos.UserCreateDto;

import br.com.wkallil.cartoes.models.UserModel;

import br.com.wkallil.cartoes.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserModel save(UserCreateDto userCreateDto) {
        UserModel user = new UserModel();
        user.setName(userCreateDto.name());
        user.setUsername(userCreateDto.username());
        user.setEmail(userCreateDto.email());
        user.setCep(userCreateDto.cep());
        user.setPassword(passwordEncoder.encode(userCreateDto.password()));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username or email already in use.");
        }


    }

}
