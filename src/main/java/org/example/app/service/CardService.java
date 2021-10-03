package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.dto.TransactionDto;
import org.example.app.exception.*;
import org.example.app.repository.CardRepository;
import org.example.app.util.CardNumberGenerator;
import org.example.framework.security.Roles;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public Card transaction(User user, TransactionDto transactionDto) {
        final var senderCardId = transactionDto.getSenderCardId();
        final var receiverCardId = transactionDto.getReceiverCardId();
        final var transferAmount = transactionDto.getTransferAmount();

        final var allByOwnerId = getAllByOwnerId(user.getId());
        final var allUserCardsId = allByOwnerId.stream().map(Card::getId).collect(Collectors.toList());

        if (!allUserCardsId.contains(senderCardId)) {
            throw new NoAccessRightsToCardException();
        }

        final var balance = allByOwnerId.stream()
                .filter(o -> o.getId() == senderCardId)
                .map(Card::getBalance)
                .findFirst().orElseThrow(NoAccessRightsToCardException::new);

        if (balance < transferAmount) {
            throw new NotEnoughMoneyException();
        }

        if (transferAmount <= 0) {
            throw new IncorrectTransferAmount();
        }

        cardRepository.receiveMoney(receiverCardId, transferAmount)
                .orElseThrow(ReceiverCardNotFoundException::new);

        final var senderCard = cardRepository.sendMoney(senderCardId, transferAmount)
                .orElseThrow(CardNotFoundException::new);

        cardRepository.registerTransaction(senderCardId, receiverCardId, transferAmount);

        return senderCard;
    }

    public List<Card> getAllByUserId(User user, Collection<String> authorities, long ownerId) {
        if (user.getId() != ownerId) {
            if (!authorities.contains(Roles.ROLE_ADMIN)) {
                throw new NoAccessRightsToCardException();
            }
        }

        return cardRepository.getAllByOwnerId(ownerId);
    }

    public List<Card> getAllByOwnerId(long ownerId) {
        return cardRepository.getAllByOwnerId(ownerId);
    }

    public Card getCardByCardId(User user, Collection<String> authorities, long cardId) {
        final var allUserCards = getAllByOwnerId(user.getId());
        final var card = cardRepository.getCardByCardId(cardId).orElseThrow(CardNotFoundException::new);

        if (!allUserCards.contains(card)) {
            if (!authorities.contains(Roles.ROLE_ADMIN)) {
                throw new NoAccessRightsToCardException();
            }
        }

        return card;
    }

    public Card orderCard(User user, Collection<String> authorities) {
        String cardNumber = CardNumberGenerator.generate();

        if (!authorities.contains(Roles.ROLE_USER)) {
            throw new CardOrderException();
        }

        return cardRepository.orderCard(user.getId(), cardNumber).orElseThrow(CardOrderException::new);
    }

    public Card blockById(User user, Collection<String> authorities, long cardId) {
        final var allByOwnerId = getAllByOwnerId(user.getId());
        final var allUserCardsId = allByOwnerId.stream().map(Card::getId).collect(Collectors.toList());

        if (!allUserCardsId.contains(cardId)) {
            if (!authorities.contains(Roles.ROLE_ADMIN)) {
                throw new NoAccessRightsToCardException();
            }
        }

        return cardRepository.blockById(cardId).orElseThrow(CardBlockingException::new);
    }


}
