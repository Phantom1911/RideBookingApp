package com.company.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Driver {

  private final String name;

  @Getter private int id;

  @Getter @Setter private Trip currentTrip;

  @Setter private boolean isAcceptingRider;

  @Getter @Setter private Location currentLocation;

  @Getter @Setter private Car car;

  private List<Trip> trips;

  private String password;

  public Driver(int id, String name, String password, Car car) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.car = car;
    this.trips = new ArrayList<>();
  }

  /**
   * Helper method to check whether driver can take new riders or not.
   *
   * @return
   */
  public boolean isAvailable() {
    return this.isAcceptingRider && this.currentTrip == null;
  }

}
