package br.com.wkallil.cartoes.repositories;

import br.com.wkallil.cartoes.models.CardModel;
import br.com.wkallil.cartoes.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<CardModel, UUID> {

    Set<CardModel> findAllByUser(UserModel user);
}
