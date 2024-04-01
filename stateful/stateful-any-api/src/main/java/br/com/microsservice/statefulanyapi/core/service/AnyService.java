package br.com.microsservice.statefulanyapi.core.service;


import br.com.microsservice.statefulanyapi.core.dto.AnyResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AnyService {

    private final TokenService tokenService;

    public AnyResponse getData(String accessToken) throws AuthenticationException {
        tokenService.validateToken(accessToken);
        var authUser = tokenService.getAuthenticatedUser(accessToken);
        var ok = HttpStatus.OK;
        return new AnyResponse(ok.name(), ok.value(), authUser);
    }

}
