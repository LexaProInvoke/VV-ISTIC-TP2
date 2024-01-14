package com.entis.travelbot.service.db.impl;

import com.entis.travelbot.entity.db.Passenger;
import com.entis.travelbot.entity.dto.request.passenger.SavePassengerRequest;
import com.entis.travelbot.entity.dto.response.PassengerResponse;
import com.entis.travelbot.exception.db.PassengerException;
import com.entis.travelbot.repository.PassengerRepository;
import com.entis.travelbot.service.db.api.PassengerService;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PassengerServiceImpl implements PassengerService {

  public PassengerServiceImpl(PassengerRepository repository) {
    this.passengerRepository = repository;
  }

  private final PassengerRepository passengerRepository;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Transactional
  @Override
  public PassengerResponse create(SavePassengerRequest request) {
    Passenger passenger = request.toEntity();
    if (request.id() != null) {
      throw PassengerException.idPresent();
    }
    passenger.setBookings(new ArrayList<>());
    passenger = passengerRepository.save(passenger);
    logger.info("Created a passenger with id {}", passenger.getId());
    return new PassengerResponse(passenger);
  }

  @Transactional(readOnly = true)
  @Override
  public PassengerResponse findById(String id) {
    return new PassengerResponse(passengerRepository.findById(UUID.fromString(id))
        .orElseThrow(() -> PassengerException.idNotFound(id)));
  }

  @Transactional(readOnly = true)
  @Override
  public Page<PassengerResponse> findAll(Pageable pageable) {
    return passengerRepository.findAll(pageable).map(PassengerResponse::new);
  }

  @Transactional
  @Override
  public PassengerResponse update(SavePassengerRequest request) {
    if (request.id() == null) {
      throw PassengerException.idNotPresent();
    }
    Passenger passenger = passengerRepository.findById(UUID.fromString(request.id()))
        .orElseThrow(() -> PassengerException.idNotFound(request.id()));
    if (StringUtils.isNotBlank(request.name())) {
      passenger.setName(request.name());
    }
    if (StringUtils.isNotBlank(request.phone())) {
      passenger.setPhone(request.phone());
    }
    if (StringUtils.isNotBlank(request.telegramUsername())) {
      passenger.setTelegramUsername(request.telegramUsername());
    }
    if (StringUtils.isNotBlank(request.chatId())) {
      passenger.setChatId(request.chatId());
    }
    passenger = passengerRepository.save(passenger);
    logger.info("Updated passenger with id %s".formatted(passenger.getId()));
    return new PassengerResponse(passenger);
  }

  @Transactional
  @Override
  public void delete(String id) {
    Passenger passenger = passengerRepository.findById(UUID.fromString(id))
        .orElse(null);
    if (passenger == null) {
      return;
    }
    passengerRepository.delete(passenger);
    logger.info("Deleted passenger with id {}", id);
  }
}