package com.entis.travelbot.entity.db;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private Integer bigLuggageCount;

  @Column(nullable = false)
  private Integer mediumLuggageCount;

  @Column(nullable = false)
  private Integer seats;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "passenger_id", nullable = false, updatable = false)
  private Passenger passenger;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false, updatable = false)
  private Trip trip;

  /*
   * Getters, setters
   *
   */

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Passenger getPassenger() {
    return passenger;
  }

  public void setPassenger(Passenger passenger) {
    this.passenger = passenger;
  }

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }



  public void setSeats(int seats) {
    this.seats = seats;
  }



  public void setBigLuggageCount(int bigLuggageCount) {
    this.bigLuggageCount = bigLuggageCount;
  }

  public int getMediumLuggageCount() {
    return mediumLuggageCount;
  }

  public void setMediumLuggageCount(int mediumLuggageCount) {
    this.mediumLuggageCount = mediumLuggageCount;
  }

}
