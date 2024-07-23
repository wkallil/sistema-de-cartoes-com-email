package br.com.wkallil.cartoes.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("CREDIT_CARD")
public class CreditCardModel extends CardModel {
    private double creditLimit;


    @Override
    public void performTransaction(double amount) {
        if (isExpired()) {
            throw new IllegalArgumentException("Card is expired");
        }
        if (getBalance() + amount <= creditLimit) {
            setBalance(getBalance() + amount);
        } else {
            throw new IllegalArgumentException("Exceeds credit limit");
        }
    }

    private boolean isExpired() {
        return LocalDate.now().isAfter(getExpirationDate());
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
}
