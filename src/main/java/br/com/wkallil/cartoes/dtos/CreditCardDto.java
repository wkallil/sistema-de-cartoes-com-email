package br.com.wkallil.cartoes.dtos;

import br.com.wkallil.cartoes.models.UserModel;

import java.time.LocalDate;

public record CreditCardDto(String cardNumber,
                            LocalDate expirationDate,
                            double creditLimit,
                            double balance,
                            UserModel user) {
}
