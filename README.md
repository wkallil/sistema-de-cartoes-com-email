# Sistema de cadastro de usuários e cartões com envio de Email

```mermaid
classDiagram
    class UserModel {
        UUID userID
        String name
        String username
        String password
        String email
        String cep
        Set<CardModel> cards
    }
    
    class CardModel {
        UUID cardId
        UserModel user
        String cardNumber
        LocalDate expirationDate
        String cvv
    }

    class CreditCardModel {
        double creditLimit
        double balance
    }

    class DebitCardModel {
        double balance
    }
    
    UserModel "1" --> "0..*" CardModel : contains
    CardModel <|-- CreditCardModel
    CardModel <|-- DebitCardModel

```


## Tecnologias usadas

- Java 21
- Spring boot
- Spring Security
- Oauth2/Jwt
- Java Mail Sender
- Intellij
- Postman
- Docker
- Swagger
- JUnit 5
- Mockito

## Meus pensamentos no desenvolvimento

* Eu queria fazer um pequeno sistema com cadastro de usuários onde eles pudessem cadastrar cartões. 
Sempre que há um cadastro realizado
ou uma movimentação nos cartões(saldo) é recebido um email.


* Foi meu primeiro projeto com o Java Mail Sender e com testes simples usando J-Unit e Mockito.