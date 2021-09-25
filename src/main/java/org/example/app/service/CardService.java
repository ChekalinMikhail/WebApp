package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.dto.TransactionDto;
import org.example.app.exception.*;
import org.example.app.repository.CardRepository;
import org.example.app.util.CardNumberGenerator;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {
  private final CardRepository cardRepository;

  public Card transaction(TransactionDto transactionDto) {
    final var senderCardId = transactionDto.getSenderCardId();
    final var receiverCardId = transactionDto.getReceiverCardId();
    final var transferAmount = transactionDto.getTransferAmount();

    final var balance = getCardByCardId(senderCardId).getBalance();
    if (balance < transferAmount && transferAmount > 0) {
      throw new NotEnoughMoneyException();
    }

    cardRepository.receiveMoney(receiverCardId, transferAmount)
            .orElseThrow(ReceiverCardNotFoundException::new);

    final var senderCard = cardRepository.sendMoney(senderCardId, transferAmount)
            .orElseThrow(CardNotFoundException::new);

    cardRepository.registerTransaction(senderCardId, receiverCardId, transferAmount);

    return senderCard;
  }

  public List<Card> getAllByOwnerId(long ownerId) {
    return cardRepository.getAllByOwnerId(ownerId);
  }

  public Card getCardByCardId(long cardId) {
    return cardRepository.getCardByCardId(cardId).orElseThrow(CardNotFoundException::new);
  }

  public Card orderCard(long ownerId) {
    String cardNumber = CardNumberGenerator.generate();
    return cardRepository.orderCard(ownerId, cardNumber).orElseThrow(CardOrderException::new);
  }

  public Card blockById(long cardId) {
    return cardRepository.blockById(cardId).orElseThrow(CarBlockingException::new);
  }


}
