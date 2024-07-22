package br.com.wkallil.cartoes.dtos;

public record UserCreateDto(String name, String username, String password, String email, String cep) {
}
