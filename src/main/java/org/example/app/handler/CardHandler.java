package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.TransactionDto;
import org.example.app.service.CardService;
import org.example.app.util.PathAttributeHelper;
import org.example.app.util.UserHelper;
import org.example.framework.security.Roles;

import java.io.IOException;

@Log
@RequiredArgsConstructor
public class CardHandler {
    private final CardService service;
    private final Gson gson;

    public void transaction(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUserFromAuth(req);
            final var transactionDto = gson.fromJson(req.getReader(), TransactionDto.class);
            final var card = service.getCardByCardId(transactionDto.getSenderCardId());
            final var allUserCards = service.getAllByOwnerId(user.getId());

            if (!allUserCards.contains(card)) {
                resp.sendError(403, "Forbidden");
                return;
            }

            final var data = service.transaction(transactionDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUserFromAuth(req);
            final var data = service.getAllByOwnerId(user.getId());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUserFromAuth(req);
            final var allUserCards = service.getAllByOwnerId(user.getId());
            final var authorities = UserHelper.getAuthorities(req);
            final var cardId = PathAttributeHelper.getLong(req, "cardId");
            final var card = service.getCardByCardId(cardId);

            if (!authorities.contains(Roles.ROLE_ADMIN) && !allUserCards.contains(card)) {
                resp.sendError(403, "Forbidden");
                return;
            }

            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(card));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void getAllByUserId(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUserFromAuth(req);
            final var authorities = UserHelper.getAuthorities(req);
            final var userId = PathAttributeHelper.getLong(req, "userId");
            final var data = service.getAllByOwnerId(userId);

            if (!authorities.contains(Roles.ROLE_ADMIN) && userId != user.getId()) {
                resp.sendError(403, "Forbidden");
                return;
            }

            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void order(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUserFromAuth(req);
            final var authorities = UserHelper.getAuthorities(req);

            if (!authorities.contains(Roles.ROLE_USER)) {
                resp.sendError(403, "Forbidden");
                return;
            }

            final var card = service.orderCard(user.getId());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(card));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUserFromAuth(req);
            final var authorities = UserHelper.getAuthorities(req);
            final var allUserCards = service.getAllByOwnerId(user.getId());
            final var cardId = PathAttributeHelper.getLong(req, "cardId");
            final var card = service.getCardByCardId(cardId);

            if (!authorities.contains(Roles.ROLE_ADMIN) && !allUserCards.contains(card)) {
                resp.sendError(403, "Forbidden");
                return;
            }

            final var blockedCard = service.blockById(cardId);

            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(blockedCard));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
