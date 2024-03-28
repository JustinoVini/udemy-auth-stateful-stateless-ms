package br.com.microsservice.statelessanyapi.core.dto;

public record AnyResponse(String status, Integer code, AuthUserResponse authUser) {
}
