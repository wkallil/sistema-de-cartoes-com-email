package br.com.wkallil.cartoes.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;



import java.time.LocalDate;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "card_type")
public abstract class CardModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID cardId;


    private String cardNumber;


    private LocalDate expirationDate;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @JsonBackReference
    private UserModel user;

    public abstract void performTransaction(double amount);

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
