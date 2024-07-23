package br.com.wkallil.cartoes.dtos;

import br.com.wkallil.cartoes.models.CardModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserCreateDto(@NotBlank String name,
                            @NotBlank @Size(min = 3, max = 50) String username,
                            @NotBlank @Size(min = 3) String password,
                            @Email @NotBlank String email,
                            @NotBlank String cep
                            ) {

}
