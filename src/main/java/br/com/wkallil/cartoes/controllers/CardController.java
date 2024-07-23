package br.com.wkallil.cartoes.controllers;

import br.com.wkallil.cartoes.dtos.CreditCardDto;
import br.com.wkallil.cartoes.models.CardModel;
import br.com.wkallil.cartoes.services.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class CardController {

    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/addCreditCard")
    public ResponseEntity<CardModel> addCard(@RequestBody CreditCardDto creditCardDto, JwtAuthenticationToken token) {

        CardModel savedCard = cardService.saveCreditCard(creditCardDto, token);
        return ResponseEntity.ok(savedCard);
    }
}
