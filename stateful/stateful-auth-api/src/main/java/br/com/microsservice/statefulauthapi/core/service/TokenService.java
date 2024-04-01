package br.com.microsservice.statefulauthapi.core.service;

import br.com.microsservice.statefulauthapi.core.dto.TokenData;
import br.com.microsservice.statefulauthapi.infra.exception.AuthenticationException;
import br.com.microsservice.statefulauthapi.infra.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor
public class TokenService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;
    private static final Long ONE_DAY_IN_SECONDS = 86400L;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public String createToken(String username) {
        var accessToken = UUID.randomUUID().toString(); // gerando token com uuid
        var data = new TokenData(username);
        var jsonData = getJsonData(data);
        redisTemplate.opsForValue().set(accessToken, jsonData); // primeira operação do redis
        redisTemplate.expireAt(accessToken, Instant.now().plusSeconds(ONE_DAY_IN_SECONDS)); // colocando expiração no redis
        return accessToken;
    }

    public TokenData getTokenData(String token) {
        var accessToken = extractToken(token);
        var jsonString = getRedisTokenValue(accessToken);
        try {
            return objectMapper.readValue(jsonString, TokenData.class);
        } catch (Exception e) {
            throw new AuthenticationException("Error extracting the authenticated user: " + e.getMessage());
        }
    }

    public void deleteRedisToken(String token) {
        var accessToken = extractToken(token);
        redisTemplate.delete(accessToken);
    }

    public Boolean validateAccessToken(String token) {
        var accessToken = extractToken(token);
        var data = getRedisTokenValue(accessToken);
        return !isEmpty(data);
    }

    private String getRedisTokenValue(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    private String getJsonData(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return "";
        }
    }

    private String extractToken(String token) {
        /*Verifica se ta vazio*/
        if (isEmpty(token)) {
            throw new ValidationException("The access token was not informed");
        }

        /*Verifica se tem espaço em branco*/
        if (token.contains(EMPTY_SPACE)) {
            /*
             * Divide o token em partes usando o espaço em branco como delimitador
             *
             * Retorna a segunda parte do token, que geralmente é o token de acesso
             * */
            return token.split(EMPTY_SPACE)[TOKEN_INDEX];
        }
        /*Se o token estiver limpo retorna o token*/
        return token;
    }

}
