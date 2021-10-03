package org.example.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.User;
import org.example.app.domain.UserRole;
import org.example.app.dto.*;
import org.example.app.exception.*;
import org.example.app.repository.UserRepository;
import org.example.app.util.PasswordRecoveryKeyGenerator;
import org.example.app.util.TokenLifeTime;
import org.example.framework.security.*;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator keyGenerator;

    @Override
    public Authentication authenticate(BasicAuthentication authentication) {
        final var login = ((String) authentication.getPrincipal());
        final var password = ((String) authentication.getCredentials());

        final var userWithPassword = repository.getByUsernameWithPassword(login).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(password, userWithPassword.getPassword())) {
            throw new PasswordNotMatchesException();
        }

        final var user = repository.findUserByLogin(login).orElseThrow(AuthenticationException::new);
        final var roles = repository.getRoles(user.getId()).stream().map(UserRole::getRole).collect(Collectors.toList());

        return new BasicAuthentication(user, null, roles, true);
    }

    @Override
    public Authentication authenticate(TokenAuthentication authentication) throws AuthenticationException {
        final var token = (String) authentication.getPrincipal();
        final var tokenWithCreateTime = repository.findToken(token).orElseThrow(AuthenticationException::new);
        final var tokenLifeTime = new Date().getTime() - tokenWithCreateTime.getCreate().getTime();

        if (tokenLifeTime > TokenLifeTime.oneHour) {
            throw new TokenLifeTimeException();
        }

        repository.updateTokenLifeTime(token);

        final var user = repository.findUserByToken(token).orElseThrow(AuthenticationException::new);
        final var roles = repository.getRoles(user.getId()).stream().map(UserRole::getRole).collect(Collectors.toList());

        return new TokenAuthentication(user, null, roles, true);
    }

    @Override
    public AnonymousAuthentication provide() {
        return new AnonymousAuthentication(new User(
                -1,
                "anonymous"
        ));
    }

    public RegistrationResponseDto register(RegistrationRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();
        final var hash = passwordEncoder.encode(password);
        final var token = keyGenerator.generateKey();
        final var saved = repository.userRegister(0, username, hash)
                .map(o -> new RegistrationResponseDto(o.getId(), o.getUsername(), token))
                .orElseThrow(RegistrationException::new);
        repository.saveToken(saved.getId(), token);
        repository.setRole(saved.getId());
        return new RegistrationResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();
        final var saved = repository.getByUsernameWithPassword(username).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, saved.getPassword())) {
            throw new PasswordNotMatchesException();
        }

        final var token = keyGenerator.generateKey();
        repository.saveNewToken(saved.getId(), token);
        return new LoginResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public void passwordRecoveryInquiry(String userName) {
        final var user = repository.getByUsername(userName).orElseThrow(UserNotFoundException::new);
        final var key = PasswordRecoveryKeyGenerator.generate();
        log.log(Level.INFO, "passwordRecovery key" + key);

        repository.createKeyForPasswordRecovery(user.getId(), key);
    }

    public LoginResponseDto passwordRecoveryСonfirmation(PasswordRecoveryDto passwordRecoveryDto) {
        final var user = repository.getByUsername(passwordRecoveryDto.getUsername()).orElseThrow(UserNotFoundException::new);
        final var passwordRecoveryKey = repository.checkPasswordRecoveryKey(user.getId())
                .orElseThrow(UnsupportedOperationException::new);
        if (!passwordRecoveryKey.getKey().equals(passwordRecoveryDto.getKey())) {
            throw new WrongPasswordRecoveryKeyException();
        }

        final var hash = passwordEncoder.encode(passwordRecoveryDto.getPassword());
        final var userWithNewPassword = repository.userRegister(user.getId(), passwordRecoveryDto.getUsername(), hash)
                .orElseThrow(UserNotFoundException::new);
        repository.deleteUsedKeyForPasswordRecovery(user.getId());
        final var token = keyGenerator.generateKey();
        repository.saveNewToken(userWithNewPassword.getId(), token);

        return new LoginResponseDto(userWithNewPassword.getId(), userWithNewPassword.getUsername(), token);
    }
}
