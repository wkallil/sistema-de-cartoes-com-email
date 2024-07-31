package br.com.wkallil.cartoes.models;

import br.com.wkallil.cartoes.dtos.TransactionDto;
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

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @JsonBackReference
    private UserModel user;

    private String cardNumber;

    private LocalDate expirationDate;

    private String cvv;

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
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

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
