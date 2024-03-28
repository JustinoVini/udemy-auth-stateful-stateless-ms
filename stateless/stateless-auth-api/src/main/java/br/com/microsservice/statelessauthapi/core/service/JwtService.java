package br.com.microsservice.statelessauthapi.core.service;

import br.com.microsservice.statelessauthapi.core.model.User;
import br.com.microsservice.statelessauthapi.infra.exception.AuthenticationException;
import br.com.microsservice.statelessauthapi.infra.exception.ValidationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;
    private static final Integer ONE_DAY_IN_HOURS = 24;

    @Value("${app.token.secret-key}")
    private String secretKey;

    public String createToken(User user) {
        /*Primeira coisa, hashmap vazio pra inserir os dados*/
        var data = new HashMap<String, String>();
        data.put("id", user.getId().toString());
        data.put("username", user.getUsername());

        // claims dados de autenticacao do usuário
        return Jwts
                .builder()
                .setClaims(data) // pega os dados do usuário e encripta
                .setExpiration(generateExpiresAt()) // seta a expiração do token
                .signWith(generateSign()) // assina o token
                .compact(); // caompacta em String
    }

    private Date generateExpiresAt() {
        /*gera a expiração no token, com base na data e hora do sistema do usuário*/
        return Date.from(LocalDateTime.now()
                .plusHours(ONE_DAY_IN_HOURS)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    private SecretKey generateSign() {
        /*Pega todos os bytes do base64 cria um secret com base no hmac, quando gerado insere no token */
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public void validateAccessToken(String token) {
        /*Valida o token de acesso*/
        var accessToken = extractToken(token);

        /*Validar token gerado*/
        try {
            /*Qualquer erro que lançar é por que o token está inválido*/
            Jwts.parserBuilder() // pega token de acesso da um build
                    .setSigningKey(generateSign()) // com essa assinatura
                    .build() // builda
                    .parseClaimsJws(accessToken) // informa com base na ass, qual o token que estamos dando decod
                    .getBody(); // recupera os claims do usuário
        } catch (Exception e) {
            throw new AuthenticationException("Invalid token " + e.getMessage());
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
