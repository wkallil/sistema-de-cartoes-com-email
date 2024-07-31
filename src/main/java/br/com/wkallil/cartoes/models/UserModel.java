package br.com.wkallil.cartoes.models;

import br.com.wkallil.cartoes.dtos.LoginRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_USERS")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID")
    private UUID userID;


    private String name;

    private String username;


    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String cep;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private Set<CardModel> cards = new HashSet<>();


    // Getters and Setters

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Set<CardModel> getCards() {
        return cards;
    }

    public void setCards(Set<CardModel> cards) {
        this.cards = cards;
    }

    public boolean isLoginCorrect(LoginRequestDto loginRequestDto, BCryptPasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(loginRequestDto.password(), this.password);
    }


}
