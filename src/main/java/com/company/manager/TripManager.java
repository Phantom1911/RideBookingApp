package com.company.manager;

import com.company.exception.DriverNotFoundException;
import com.company.exception.InvalidRideParamException;
import com.company.exception.TripNotFoundException;
import com.company.exception.TripStatusException;
import com.company.model.CarType;
import com.company.model.Driver;
import com.company.model.Location;
import com.company.model.Rider;
import com.company.model.Trip;
import com.company.model.TripStatus;
import com.company.strategy.DistanceFindingStrategy;
import com.company.strategy.DriverMatchingStrategy;
import com.company.strategy.PricingStrategy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;

/** TripManager class is used to manage all riders and drivers. */
@Data
public class TripManager {

  private RiderManager riderManager;
  private DriverManager driverManager;

  private static final Double MAX_TRIP_DISTANCE = 100.00;
  private static final Double LOOK_WITHIN_RADIUS = 10.00;

    /** Mapping of trip id with it's associated trips. */
    private Map<String, Trip> trips = new HashMap<>();

  public TripManager(
      final RiderManager riderManager,
      final DriverManager driverManager) {
    this.riderManager = riderManager;
    this.driverManager = driverManager;
  }

  /**
   * Method to create a trip for rider.
   *
   * @param rider Object.
   * @param origin Integer.
   * @param destination Integer.
   * @return Trip Id.
   */
  public String createTrip(
      final Rider rider, final Location origin, final Location destination, PricingStrategy pricingStrategy,
          DistanceFindingStrategy distanceFindingStrategy, DriverMatchingStrategy driverMatchingStrategy,
          CarType requestedCarType) {

    // Throw exception if origin and destination are farther than max distance allowed
    if (distanceFindingStrategy.distanceBetweenLocations(origin, destination) > MAX_TRIP_DISTANCE) {
      throw new InvalidRideParamException(
          "Origin and destination are more than max distance that are allowed");
    }

    // find all the available drivers within the specified radius
    List<Driver> driversWithinSpecifedRadius = driverManager.getDriversWithinRadius(origin,
            LOOK_WITHIN_RADIUS, distanceFindingStrategy);

    Optional<Driver> matchedDriver =
        driverMatchingStrategy.findDriver(rider, driversWithinSpecifedRadius, origin, destination, requestedCarType);

    if (!matchedDriver.isPresent()) {
      throw new DriverNotFoundException("Driver not found, Please try after some time");
    }

    // Create a trip for rider if all's good.
    Driver driver = matchedDriver.get();

    if (driver.getCar().getCarType() != requestedCarType) {
        System.out.println("Congrats! You have been upgraded at no extra cost!");
    }

    // fare calculation is on the basis of requested car type and not on basis of actual car type allotted
    double fare = calculateFare(rider, origin, destination,  pricingStrategy, requestedCarType, distanceFindingStrategy);

    // we update the trip with allotted car type and not the requested car type
    // trip is set to booked status
    Trip trip = new Trip(rider, driver, origin, destination, fare, driver.getCar());

    // make changes to Driver and Rider classes using manager classes
    riderManager.updateTripForRider(rider.getId(), trip);

    driverManager.updateTripForDriver(driver.getId(), trip);

    // having current trip for driver is required so that we know that he is currently on some other trip when finding drivers for a ride
    driver.setCurrentTrip(trip);

    trips.put(trip.getId(), trip);

    return trip.getId();
  }

  /**
   * Update the trip details using Trip Id.
   *
   * @param tripId String.
   * @param origin Integer.
   * @param destination Integer.
   */
  // update trip doesn't take in car type as param since you can't change car midway
  // new fare calculation for updated trip will be on the basis of the allotted car type of the trip
  // update trip allows just to change the destination of the trip
  // origin in case of updateTrip means the current location of the cab
  public void updateTrip(
      final String tripId, final Location origin, final Location destination, DistanceFindingStrategy distanceFindingStrategy,
          PricingStrategy pricingStrategy) {

      // Throw exception if origin and destination are farther than max distance allowed
      if (distanceFindingStrategy.distanceBetweenLocations(origin, destination) > MAX_TRIP_DISTANCE) {
          throw new InvalidRideParamException(
                  "Origin and destination are more than max distance that are allowed");
      }

    Optional<Trip> optionalTrip = this.getTrip(tripId);

    if (!optionalTrip.isPresent()) {
      throw new TripNotFoundException(
          "No Trip found for the given Id = " + tripId + ", please try with valid Trip Id.");
    }

    Trip trip = optionalTrip.get();

    // trip can only be updated if it is BOOKED or IN_PROGRESS
    if (trip.getStatus().equals(TripStatus.COMPLETED)
        || trip.getStatus().equals(TripStatus.WITHDRAWN)) {
      throw new TripStatusException(
          "Trip has already been completed or withdrawn try with valid Trip Id.");
    }

    double fare = calculateFare(trip.getRider(), origin, destination,  pricingStrategy, trip.getCar().getCarType(), distanceFindingStrategy);

    trip.updateTrip(origin, destination, fare);
  }

  /**
   * Method to withdraw trip using trip Id.
   *
   * @param tripId String.
   */
  public void withdrawTrip(final String tripId) {

    Optional<Trip> optionalTrip = this.getTrip(tripId);

    if (!optionalTrip.isPresent()) {
      throw new TripNotFoundException(
          "No Trip found for the given Id = " + tripId + ", please try with valid Trip Id.");
    }

    Trip trip = optionalTrip.get();

    if (! trip.getStatus().equals(TripStatus.BOOKED)) {
      throw new TripStatusException("Trip has already started or completed, can't withdraw now.");
    }

    Driver driver = trip.getDriver();
    driver.setCurrentTrip(null);
    trip.withdrawTrip();
  }

  /** start the trip */
  public void startTrip(final String tripId) {
      Optional<Trip> optionalTrip = this.getTrip(tripId);

      if (!optionalTrip.isPresent()) {
          throw new TripNotFoundException(
                  "No Trip found for the given Id = " + tripId + ", please try with valid Trip Id.");
      }

      Trip trip = optionalTrip.get();

      if (! trip.getStatus().equals(TripStatus.BOOKED)) {
          throw new TripStatusException("Trip has already started or completed or withdrawn, can't start now.");
      }

      // no need to set the current trip for driver here since it has already been set at the time of booking
      // update the location of the cab => driver reached origin to pick up passenger
      trip.getDriver().setCurrentLocation(trip.getOrigin());
      trip.startTrip();
  }

    /** end the trip
     *  returns the fare of the trip
     * */
    public double endTrip(final String tripId) {
        Optional<Trip> optionalTrip = this.getTrip(tripId);

        if (!optionalTrip.isPresent()) {
            throw new TripNotFoundException(
                    "No Trip found for the given Id = " + tripId + ", please try with valid Trip Id.");
        }

        Trip trip = optionalTrip.get();

        if (! trip.getStatus().equals(TripStatus.IN_PROGRESS)) {
            throw new TripStatusException("Trip has already completed or withdrawn, can't end now.");
        }

        Driver driver = trip.getDriver();
        // update the current trip for driver here
        driver.setCurrentTrip(null);
        // update the location of the cab => driver reached destination to drop the passenger
        driver.setCurrentLocation(trip.getDestination());
        trip.endTrip();

        return trip.getFare();
    }

  /**
   * Helper method to get the respective driver for the given rider.
   *
   * @param tripId Integer.
   * @return Driver.
   */
  public Optional<Driver> getDriverForTrip(final String tripId) {
    Optional<Trip> trip =
        Optional.ofNullable(trips.get(tripId));

    return Optional.of(trip.get().getDriver());
  }

  /**
   * Helper Method to check if the given rider is preferred or not.
   *
   * @param rider Object.
   * @return Boolean.
   */
  private boolean isRiderPreferred(final Rider rider) {
    return riderManager.tripHistoryInternal(rider).size() >= 10;
  }

  /**
   * Helper method to get trip for the given Trip Id.
   *
   * @param tripId Integer.
   * @return Trip.
   */
  private Optional<Trip> getTrip(final String tripId) {
      return Optional.ofNullable(trips.get(tripId));
  }

  /**
   * Helper method to calculate fare.
   *
   * @param rider Object.
   * @param origin Integer.
   * @param destination Integer.
   * @return double.
   */
  private double calculateFare(final Rider rider, Location origin, Location destination, PricingStrategy pricingStrategy,
          CarType carTypeRequested, DistanceFindingStrategy distanceFindingStrategy) {
    return isRiderPreferred(rider)
        ? pricingStrategy.calculateFareForPreferred(carTypeRequested, origin, destination, distanceFindingStrategy)
        : pricingStrategy.calculateFare(carTypeRequested, origin, destination, distanceFindingStrategy);
  }
}
