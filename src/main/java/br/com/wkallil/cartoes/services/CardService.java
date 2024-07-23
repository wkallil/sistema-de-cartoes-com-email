package br.com.wkallil.cartoes.services;

import br.com.wkallil.cartoes.dtos.CreditCardDto;
import br.com.wkallil.cartoes.dtos.DebitCardDto;
import br.com.wkallil.cartoes.models.CardModel;
import br.com.wkallil.cartoes.models.CreditCardModel;
import br.com.wkallil.cartoes.models.DebitCardModel;
import br.com.wkallil.cartoes.repositories.CardRepository;
import br.com.wkallil.cartoes.repositories.UserRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;

        this.userRepository = userRepository;
    }

    public CardModel saveCreditCard(CreditCardDto creditCard, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        var card = new CreditCardModel();
        card.setCardNumber(creditCard.cardNumber());
        card.setExpirationDate(creditCard.expirationDate());
        card.setCreditLimit(creditCard.creditLimit());
        card.setBalance(creditCard.creditLimit()); // Credit cards start with zero balance.
        card.setUser(user);

        return cardRepository.save(card);
    }

    @Transactional
    public CardModel saveDebitCard(DebitCardDto debitCard, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        var card = new DebitCardModel();
        card.setCardNumber(debitCard.cardNumber());
        card.setExpirationDate(debitCard.expirationDate());
        card.setBalance(debitCard.initialBalance());
        card.setUser(user);

        return cardRepository.save(card);
    }

    public void performTransaction(UUID cardId, double amount) {
        CardModel cardToUpdate = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not be found."));

        cardToUpdate.performTransaction(amount);

        cardRepository.save(cardToUpdate);
    }


}
