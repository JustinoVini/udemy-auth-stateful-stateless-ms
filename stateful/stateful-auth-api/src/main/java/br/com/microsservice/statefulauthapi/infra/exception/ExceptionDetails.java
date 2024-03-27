package br.com.microsservice.statefulauthapi.infra.exception;

public record ExceptionDetails(int status, String message) {
}
