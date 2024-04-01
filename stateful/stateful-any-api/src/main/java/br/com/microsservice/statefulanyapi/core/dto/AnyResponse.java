package br.com.microsservice.statefulanyapi.core.dto;

public record AnyResponse(String status, Integer code, AuthUserResponse authUser) {
}
