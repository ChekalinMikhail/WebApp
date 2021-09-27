package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.LoginRequestDto;
import org.example.app.dto.PasswordRecoveryDto;
import org.example.app.dto.RegistrationRequestDto;
import org.example.app.service.UserService;
import org.example.app.util.UserHelper;

import java.io.IOException;


@Log
@RequiredArgsConstructor
public class UserHandler {
    private final UserService service;
    private final Gson gson;

    public void register(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final RegistrationRequestDto requestDto;
            requestDto = gson.fromJson(req.getReader(), RegistrationRequestDto.class);
            final var responseDto = service.register(requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var requestDto = gson.fromJson(req.getReader(), LoginRequestDto.class);
            final var responseDto = service.login(requestDto);
            resp.setHeader("Set-Cookie", "token=" + responseDto.getToken() + "; Secure; HttpOnly");
//            final var cookie = new Cookie("token", responseDto.getToken());
//            cookie.setHttpOnly(true);
//            cookie.setSecure(true);
//            resp.addCookie(cookie);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void passwordRecoveryInquiry(HttpServletRequest req, HttpServletResponse resp) {
        final var userName = UserHelper.getUsernameFromUrl(req);
        service.passwordRecoveryInquiry(userName);
    }

    public void passwordRecoveryConfirmation(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var passwordRecoveryDto = gson.fromJson(req.getReader(), PasswordRecoveryDto.class);
            final var loginResponseDto = service.passwordRecoveryСonfirmation(passwordRecoveryDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(loginResponseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
