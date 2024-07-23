package br.com.wkallil.cartoes.dtos;

import br.com.wkallil.cartoes.models.UserModel;

import java.time.LocalDate;

public record DebitCardDto(String cardNumber, LocalDate expirationDate, double balance, double initialBalance, UserModel user) {
}
