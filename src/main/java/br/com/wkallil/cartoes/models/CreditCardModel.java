package br.com.wkallil.cartoes.models;

import br.com.wkallil.cartoes.utils.Card;

public class CreditCardModel extends Card {
    private double limit;
    private double balance;

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
