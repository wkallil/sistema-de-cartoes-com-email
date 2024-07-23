package br.com.wkallil.cartoes.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;


@Entity
@DiscriminatorValue("DEBIT")
public class DebitCardModel extends CardModel {


    @Override
    public void performTransaction(double amount) {
        if (isExpired()) {
            throw new IllegalStateException("Card is expired");
        }
       if(amount <= getBalance()) {
           setBalance(getBalance() - amount);
       } else {
           throw new IllegalArgumentException("Insufficient balance");
       }
    }

    private boolean isExpired() {
        return LocalDate.now().isAfter(getExpirationDate());
    }


}
