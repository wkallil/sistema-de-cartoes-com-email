package br.com.wkallil.cartoes.models;

import br.com.wkallil.cartoes.utils.Card;
import org.hibernate.validator.constraints.UUID;

public class DebitCard extends Card {
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
