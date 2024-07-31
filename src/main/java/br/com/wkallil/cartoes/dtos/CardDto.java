package br.com.wkallil.cartoes.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record CardDto(UUID id, String cardNumber, LocalDate expirationDate, String cvv, String cardType, double balance,
                      double limit) {
}
