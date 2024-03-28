package br.com.microsservice.statelessanyapi.core.service;

import br.com.microsservice.statelessanyapi.core.dto.AuthUserResponse;
import br.com.microsservice.statelessanyapi.infra.exception.AuthenticationException;
import br.com.microsservice.statelessanyapi.infra.exception.ValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;

    @Value("${app.token.secret-key}")
    private String secretKey;

    public AuthUserResponse getAuthenticatedUser(String token) {
        var tokenClaims = getClaims(token);
        var userId = Integer.valueOf((String) tokenClaims.get("id"));
        return new AuthUserResponse(userId, (String) tokenClaims.get("username"));
    }

    private Claims getClaims(String token) {
        /*Valida o token de acesso*/
        var accessToken = extractToken(token);

        /*Validar token gerado*/
        try {
            /*Qualquer erro que lançar é por que o token está inválido*/
            return Jwts.parserBuilder() // pega token de acesso da um build
                    .setSigningKey(generateSign()) // com essa assinatura
                    .build() // builda
                    .parseClaimsJws(accessToken) // informa com base na ass, qual o token que estamos dando decod
                    .getBody(); // recupera os claims do usuário
        } catch (Exception e) {
            throw new AuthenticationException("Invalid token " + e.getMessage());
        }
    }

    public void validateAccessToken(String token) {
        getClaims(token); // se nada der errado continua
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

    private SecretKey generateSign() {
        /*Pega todos os bytes do base64 cria um secret com base no hmac, quando gerado insere no token */
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

}
