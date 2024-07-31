package br.com.wkallil.cartoes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import br.com.wkallil.cartoes.models.CardModel;
import br.com.wkallil.cartoes.models.CreditCardModel;
import br.com.wkallil.cartoes.models.DebitCardModel;
import br.com.wkallil.cartoes.models.UserModel;
import br.com.wkallil.cartoes.repositories.CardRepository;
import br.com.wkallil.cartoes.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private CardService cardService;

    private JwtAuthenticationToken token;
    private UserModel user;
    private DebitCardModel debitCard;
    private CreditCardModel creditCard;

    @BeforeEach
    void setUp() {
        UUID validUserId = UUID.randomUUID();
        token = Mockito.mock(JwtAuthenticationToken.class);
        when(token.getName()).thenReturn(validUserId.toString());

        user = new UserModel();
        user.setUserID(validUserId);
        user.setEmail("user@example.com");

        debitCard = new DebitCardModel();
        debitCard.setCardNumber("123456");
        debitCard.setBalance(1000.0);
        debitCard.setUser(user);

        creditCard = new CreditCardModel();
        creditCard.setCardNumber("654321");
        creditCard.setBalance(500.0);
        creditCard.setCreditLimit(1000.0);
        creditCard.setUser(user);
    }

    @Test
    void testCreateDebitCardSuccess() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.save(any(CardModel.class))).thenReturn(debitCard);

        CardModel result = cardService.createCard(token, debitCard);

        assertEquals(debitCard.getCardNumber(), result.getCardNumber());
        assertEquals(debitCard.getUser(), result.getUser());
    }

    @Test
    void testCreateCreditCardSuccess() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.save(any(CardModel.class))).thenReturn(creditCard);

        CardModel result = cardService.createCard(token, creditCard);

        assertEquals(creditCard.getCardNumber(), result.getCardNumber());
        assertEquals(creditCard.getUser(), result.getUser());
    }

    @Test
    void testUpdateBalanceDebitCardSuccess() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(debitCard.getCardId())).thenReturn(Optional.of(debitCard));
        when(cardRepository.save(any(CardModel.class))).thenReturn(debitCard);
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        CardModel result = cardService.updateBalance(debitCard.getCardId(), 200.0, token);

        assertEquals(1200.0, debitCard.getBalance());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testUpdateBalanceCreditCardSuccess() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(creditCard.getCardId())).thenReturn(Optional.of(creditCard));
        when(cardRepository.save(any(CardModel.class))).thenReturn(creditCard);
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        CardModel result = cardService.updateBalance(creditCard.getCardId(), 200.0, token);

        assertEquals(700.0, creditCard.getBalance());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testUpdateBalanceCreditCardInsufficientLimit() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(creditCard.getCardId())).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalStateException.class, () -> cardService.updateBalance(creditCard.getCardId(), 600.0, token));
    }

    @Test
    void testPayDebitCardSuccess() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(debitCard.getCardId())).thenReturn(Optional.of(debitCard));
        when(cardRepository.save(any(CardModel.class))).thenReturn(debitCard);
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        CardModel result = cardService.pay(debitCard.getCardId(), 200.0, token);

        assertEquals(800.0, debitCard.getBalance());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testPayDebitCardInsufficientBalance() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(debitCard.getCardId())).thenReturn(Optional.of(debitCard));

        assertThrows(IllegalStateException.class, () -> cardService.pay(debitCard.getCardId(), 1200.0, token));
    }

    @Test
    void testPayCreditCardSuccess() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(creditCard.getCardId())).thenReturn(Optional.of(creditCard));
        when(cardRepository.save(any(CardModel.class))).thenReturn(creditCard);
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        CardModel result = cardService.pay(creditCard.getCardId(), 200.0, token);

        assertEquals(300.0, creditCard.getBalance());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testPayCreditCardInsufficientBalance() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(creditCard.getCardId())).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalStateException.class, () -> cardService.pay(creditCard.getCardId(), 600.0, token));
    }

    @Test
    void testGetCardsSuccess() {
        Set<CardModel> cards = Set.of(debitCard, creditCard);
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUser(user)).thenReturn(cards);

        Set<CardModel> result = cardService.getCards(token);

        assertEquals(cards.size(), result.size());
    }

    @Test
    void testCreateCardUserNotFound() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(token, debitCard));
    }

    @Test
    void testUpdateBalanceCardNotFound() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(debitCard.getCardId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.updateBalance(debitCard.getCardId(), 200.0, token));
    }

    @Test
    void testPayCardNotFound() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.of(user));
        when(cardRepository.findById(debitCard.getCardId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.pay(debitCard.getCardId(), 200.0, token));
    }

    @Test
    void testGetCardsUserNotFound() {
        when(userRepository.findById(UUID.fromString(token.getName()))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cardService.getCards(token));
    }
}