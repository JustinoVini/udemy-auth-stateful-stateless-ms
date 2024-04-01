package br.com.microsservice.statefulauthapi.core.service;

import br.com.microsservice.statefulauthapi.core.dto.AuthRequest;
import br.com.microsservice.statefulauthapi.core.dto.AuthUserResponse;
import br.com.microsservice.statefulauthapi.core.dto.TokenDTO;
import br.com.microsservice.statefulauthapi.core.model.User;
import br.com.microsservice.statefulauthapi.core.repository.UserRepository;
import br.com.microsservice.statefulauthapi.infra.exception.AuthenticationException;
import br.com.microsservice.statefulauthapi.infra.exception.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    public TokenDTO login(AuthRequest request) {
        var user = findByUsername(request.username());
        var accessToken = tokenService.createToken(user.getUsername());
        validatePassword(request.password(), user.getPassword());
        return new TokenDTO(accessToken);
    }

    public AuthUserResponse getAuthenticatedUser(String accessToken) {
        var tokenData = tokenService.getTokenData(accessToken);
        var user = findByUsername(tokenData.username());
        return new AuthUserResponse(user.getId(), user.getUsername());
    }

    public void logout(String accessToken) {
        tokenService.deleteRedisToken(accessToken);
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found"));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (isEmpty(rawPassword)) {
            throw new ValidationException("The password must be informed");
        }

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ValidationException("The password is incorrect");
        }
    }

    public TokenDTO validateToken(String accessToken) {
        validateExistingToken(accessToken);
        var valid = tokenService.validateAccessToken(accessToken);
        if (valid) {
            return new TokenDTO(accessToken);
        }
        throw new AuthenticationException("Ivalid token!");
    }

    private void validateExistingToken(String accessToken) {
        if (isEmpty(accessToken)) {
            throw new ValidationException("The access token must be informed!");
        }
    }

}
