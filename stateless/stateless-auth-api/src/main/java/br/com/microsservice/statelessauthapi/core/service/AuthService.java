package br.com.microsservice.statelessauthapi.core.service;

import br.com.microsservice.statelessauthapi.core.dto.AuthRequest;
import br.com.microsservice.statelessauthapi.core.dto.TokenDTO;
import br.com.microsservice.statelessauthapi.core.repository.UserRepository;
import br.com.microsservice.statelessauthapi.infra.exception.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor
public class AuthService { // vai fazer

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    public TokenDTO login(AuthRequest request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ValidationException("User not found"));

        var accessToken = jwtService.createToken(user);
        validatePassword(request.password(), user.getPassword());
        return new TokenDTO(accessToken);
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ValidationException("The password is incorrect");
        }
    }

    public TokenDTO validateToken(String accessToken) {
        validateExistingToken(accessToken);
        jwtService.validateAccessToken(accessToken);
        return new TokenDTO(accessToken);
    }

    private void validateExistingToken(String accessToken) {
        if (isEmpty(accessToken)) {
            throw new ValidationException("The access token must be informed!");
        }
    }

}
