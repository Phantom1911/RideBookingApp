package com.company.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class Trip {

  @Getter private String id;

  @Getter private Rider rider;

  @Getter private Driver driver;

  @Getter @Setter private Location origin;
  @Getter @Setter private Location destination;

  @Getter private double fare;

  @Getter private TripStatus status;

  @Getter @Setter
  Car car;


  public Trip(
      final Rider rider,
      final Driver driver,
      final Location origin,
      final Location destination,
      final double fare, Car car) {
    this.id = UUID.randomUUID().toString();
    this.rider = rider;
    this.driver = driver;
    this.origin = origin;
    this.destination = destination;
    this.fare = fare;
    this.car = car;
    this.status = TripStatus.BOOKED;
  }

  /** The trip can be updated with a new origin, destination and fare when IN_PROGRESS */
  public void updateTrip(
      final Location origin, final Location destination, final double fare) {
    this.origin = origin;
    this.destination = destination;
    this.fare = fare;
  }

    public void startTrip() {
        this.status = TripStatus.IN_PROGRESS;
    }

  public void endTrip() {
    this.status = TripStatus.COMPLETED;
  }

  public void withdrawTrip() {
    this.status = TripStatus.WITHDRAWN;
  }
}
