package br.com.wkallil.cartoes.dtos;

public record LoginResponseDto(String accessToken, Long expiresIn) {
}
