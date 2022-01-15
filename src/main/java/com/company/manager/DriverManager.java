package com.company.manager;

import com.company.exception.CarAlreadyPresentException;
import com.company.exception.DriverAlreadyPresentException;
import com.company.exception.DriverNotFoundException;
import com.company.exception.RiderNotFoundException;
import com.company.model.Car;
import com.company.model.Driver;
import com.company.model.Location;
import com.company.model.Trip;
import com.company.strategy.DistanceFindingStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Driver manager class is used to manage all operations related to driver. */
public class DriverManager {

    // mapping of driver id to the driver
  private Map<Integer, Driver> drivers = new HashMap<>();

  // mapping of driver id to the car he drives
  private Map<Integer, Car> cars = new HashMap<>();

  /**
   * Method to register a new Driver into the application.
   *
   * @param driver Object
   * @throws DriverAlreadyPresentException exception if driver is already present
   */
  public void createDriver(final Driver driver) {
    if (drivers.containsKey(driver.getId())) {
      throw new DriverAlreadyPresentException(
          "Driver with driver id = " + driver.getId() + " already present, try with different Id.");
    }

    if (cars.containsValue(driver.getCar())) {
        throw new CarAlreadyPresentException(
                "Car is already present with another driver with driver id = " + driver.getId());
    }

    drivers.put(driver.getId(), driver);
    cars.put(driver.getId(), driver.getCar());
  }

  /**
   * Method to update whether a driver is accepting ride or not.
   *
   * @param driverId integer
   * @param newAvailability boolean
   * @throws DriverNotFoundException If Driver not found for the given driver id.
   */
  public void updateDriverAvailability(final int driverId, boolean newAvailability) {
    if (!drivers.containsKey(driverId)) {
      throw new DriverNotFoundException(
          "No driver with driver id = " + driverId + ", try with correct driver Id.");
    }

    drivers.get(driverId).setAcceptingRider(newAvailability);
  }

    /**
     * Update location of the driver
     */
    public void updateDriverLocation(final int driverId, Location location) {
        if (!drivers.containsKey(driverId)) {
            throw new DriverNotFoundException(
                    "No driver with driver id = " + driverId + ", try with correct driver Id.");
        }

        drivers.get(driverId).setCurrentLocation(location);
    }

    /**
     * Method to return the list of all available drivers.
     * @return
     */
  public List<Driver> getDrivers() {
      // get the drivers who are not currently on a trip and are accepting rides at the moment
    return drivers.values().stream().filter(Driver::isAcceptingRider)
            .collect(Collectors.toList());
  }

  public List<Driver> getDriversWithinRadius(Location origin, double radius, DistanceFindingStrategy distanceFindingStrategy) {
      ArrayList<Driver> driversWithinRadius = new ArrayList<Driver>();
      // get all the drivers who are not currently on a trip and are accepting rides at the moment
      List<Driver> allDrivers = getDrivers();
      for (Driver driver : allDrivers) {
          if (distanceFindingStrategy.distanceBetweenLocations(driver.getCurrentLocation(), origin) <= radius) {
              driversWithinRadius.add(driver);
          }
      }

      return driversWithinRadius;
  }

  /** Set current trip for the driver */
  public void setCurrentTrip(int driverId, Trip trip) {
      drivers.get(driverId).setCurrentTrip(trip);
  }

    /**
     * Updates driver with the trips that he has completed / withdrawn / in-progress
     */
    public void updateTripForDriver(final int driverId, Trip trip) {
        if (!drivers.containsKey(driverId)) {
            throw new DriverNotFoundException("Driver with driver Id = " + driverId + " not found.");
        }

        drivers.get(driverId).getTrips().add(trip);
    }

    /** Driver makes himself available to take the trips */
    public void makeAvailable(final int driverId, Location currentLocation) {
        if (!drivers.containsKey(driverId)) {
            throw new DriverNotFoundException("Driver with driver Id = " + driverId + " not found.");
        }

        Driver driver = drivers.get(driverId);
        driver.setCurrentLocation(currentLocation);
        driver.setAcceptingRider(true);
    }
}
