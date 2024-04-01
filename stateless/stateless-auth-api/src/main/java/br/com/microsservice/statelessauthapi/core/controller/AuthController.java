package br.com.microsservice.statelessauthapi.core.controller;

import br.com.microsservice.statelessauthapi.core.dto.AuthRequest;
import br.com.microsservice.statelessauthapi.core.dto.TokenDTO;
import br.com.microsservice.statelessauthapi.core.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public TokenDTO login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("token/validate")
    public TokenDTO validateToken(@RequestHeader String accessToken) {
        return authService.validateToken(accessToken);
    }

}
