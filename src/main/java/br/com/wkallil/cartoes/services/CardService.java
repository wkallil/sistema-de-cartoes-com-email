package br.com.wkallil.cartoes.services;


import br.com.wkallil.cartoes.models.CardModel;
import br.com.wkallil.cartoes.models.CreditCardModel;
import br.com.wkallil.cartoes.models.DebitCardModel;
import br.com.wkallil.cartoes.repositories.CardRepository;
import br.com.wkallil.cartoes.repositories.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;

    public CardService(CardRepository cardRepository, UserRepository userRepository, JavaMailSender emailSender) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public CardModel createCard(JwtAuthenticationToken token, CardModel card) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        card.setUser(user);
        return cardRepository.save(card);
    }

    public CardModel updateBalance(UUID cardId, double amount, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        var card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found."));
        if (card instanceof DebitCardModel debitCard) {
            debitCard.setBalance(debitCard.getBalance() + amount);
            // Enviar Email de compra realizada de uma cartao de debito
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Compra realizada com sucesso!");
            message.setText("Uma compra no seu cartão " + debitCard.getCardNumber() + " no valor de R$ " + amount +
                    " foi realizada com sucesso. Você ainda tem : R$ " + (debitCard.getBalance() + amount) +
                    " disponíveis para uso.");
            emailSender.send(message);
        } else if (card instanceof CreditCardModel creditCard) {
            var balance = creditCard.getBalance() + amount;
            if (balance > creditCard.getCreditLimit()) {
                throw new IllegalStateException("Insufficient credit limit.");
            }
            if (balance == creditCard.getCreditLimit()) {
                System.out.println("Credit card is reached the credit limit.");
            }
            creditCard.setBalance(balance);
            // Enviar Email de compra realizada de uma cartao de credito
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Compra realizada com sucesso!");
            message.setText("Uma compra no seu cartão " + creditCard.getCardNumber() + " no valor de R$ " + amount +
                    " foi realizada com sucesso. Você ainda tem : R$ " + (creditCard.getCreditLimit() - creditCard.getBalance()) +
                    " disponíveis para uso.");
            emailSender.send(message);
        }
        return cardRepository.save(card);
    }

    public CardModel pay(UUID cardId, double amount, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        var card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Card not found."));
        if (card instanceof DebitCardModel debitCard) {
            if (debitCard.getBalance() < amount) {
                throw new IllegalStateException("Insufficient balance.");
            }
            debitCard.setBalance(debitCard.getBalance() - amount);
            // Enviar Email de pagamento realizado de uma cartao de debito
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Pagamento realizado com sucesso!");
            message.setText("Recebemos seu pagamento no cartão " + debitCard.getCardNumber() + " no valor de R$ " + amount +
                    ". Seu saldo é agora : R$ " + debitCard.getBalance());
            emailSender.send(message);
        } else if (card instanceof CreditCardModel creditCard) {
            if (creditCard.getBalance() < amount) {
                throw new IllegalStateException("Insufficient balance.");
            }
            creditCard.setBalance(creditCard.getBalance() - amount);
            // Enviar Email de pagamento realizado de uma cartao de credito
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Pagamento realizado com sucesso!");
            message.setText("Recebemos seu pagamento no cartão " + creditCard.getCardNumber() + " no valor de R$ " + amount +
                    ". Seu saldo é agora : R$ " + (creditCard.getCreditLimit() - creditCard.getBalance()));
            emailSender.send(message);
        }
        return cardRepository.save(card);
    }

    public Set<CardModel> getCards(JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return cardRepository.findAllByUser(user);
    }

}

