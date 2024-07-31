package br.com.wkallil.cartoes.controllers;

import br.com.wkallil.cartoes.dtos.CardDto;
import br.com.wkallil.cartoes.dtos.TransactionDto;
import br.com.wkallil.cartoes.models.CardModel;
import br.com.wkallil.cartoes.models.CreditCardModel;
import br.com.wkallil.cartoes.models.DebitCardModel;
import br.com.wkallil.cartoes.models.UserModel;
import br.com.wkallil.cartoes.services.CardService;
import br.com.wkallil.cartoes.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @PostMapping("/credit")
    public ResponseEntity<CardDto> createCreditCard(@RequestBody CreditCardModel card, JwtAuthenticationToken token) {
        var savedCard = (CreditCardModel) cardService.createCard(token, card);
        CardDto cardDto = new CardDto(savedCard.getCardId(), savedCard.getCardNumber(),
                savedCard.getExpirationDate(), savedCard.getCvv(),
                "Credit_Card",
                savedCard.getBalance(), savedCard.getCreditLimit());
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/debit")
    public ResponseEntity<CardDto> createDebitCard(@RequestBody DebitCardModel card, JwtAuthenticationToken token) {
        var savedCard = (DebitCardModel) cardService.createCard(token, card);
        CardDto cardDto = new CardDto(savedCard.getCardId(), savedCard.getCardNumber(),
                savedCard.getExpirationDate(), savedCard.getCvv(),
                "Debit_Card", savedCard.getBalance(), 0.0);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping
    public ResponseEntity<Set<CardDto>> getCards(JwtAuthenticationToken token) {
        Set<CardModel> cards = cardService.getCards(token);
        Set<CardDto> cardDtos = cards.stream().map(card -> {
            if (card instanceof CreditCardModel creditCard) {
                return new CardDto(creditCard.getCardId(), creditCard.getCardNumber(),
                        creditCard.getExpirationDate(), creditCard.getCvv(),
                        "Credit_Card", creditCard.getBalance(), creditCard.getCreditLimit());
            } else if (card instanceof DebitCardModel debitCard) {
                return new CardDto(debitCard.getCardId(), debitCard.getCardNumber(),
                        debitCard.getExpirationDate(), debitCard.getCvv(),
                        "Debit_Card", debitCard.getBalance(), 0.0);
            }
            return null;
        }).collect(Collectors.toSet());
        return ResponseEntity.ok(cardDtos);
    }

    @PostMapping("/{cardId}/transactions")
    public ResponseEntity<CardModel> updateBalance(@PathVariable("cardId") UUID cardId,
                                                          @RequestBody TransactionDto transactionDto,
                                                          JwtAuthenticationToken token) {
        CardModel card = cardService.updateBalance(cardId, transactionDto.amount(), token);
        return ResponseEntity.ok(card);
    }

    @PostMapping("/{cardId}/pay")
    public ResponseEntity<CardModel> pay(@PathVariable("cardId") UUID cardId,
                                                        @RequestBody TransactionDto transactionDto,
                                                        JwtAuthenticationToken token) {
        CardModel card = cardService.pay(cardId, transactionDto.amount(), token);
        return ResponseEntity.ok(card);
    }
}
