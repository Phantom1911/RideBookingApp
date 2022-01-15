package com.company.manager;

import com.company.exception.InvalidPasswordException;
import com.company.exception.RiderAlreadyPresentException;
import com.company.exception.RiderNotFoundException;
import com.company.model.Rider;
import com.company.model.Trip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Rider class is used to manage all riders present in the ride application. */
public class RiderManager {

  /** Mapping of rider id to rider. This is updated when a new rider registers */
  private Map<Integer, Rider> riders = new HashMap<>();

  /**
   * Method to add a new rider in the application.
   *
   * @param rider object
   * @throws RiderAlreadyPresentException exception
   */
  public void createRider(final Rider rider) {
    if (riders.containsKey(rider.getId())) {
      throw new RiderAlreadyPresentException(
          "Rider with rider Id = " + rider.getId() + " already present, try with different Id.");
    }

    riders.put(rider.getId(), rider);
  }

  /**
   * Method to get the Rider object for the given Rider ID.
   *
   * @param riderId integer
   * @return Rider Object
   * @throws RiderNotFoundException exception
   */
  public Rider getRider(final int riderId) {
    if (!riders.containsKey(riderId)) {
      throw new RiderNotFoundException("Rider with rider Id = " + riderId + " not found.");
    }

    return riders.get(riderId);
  }

    /**
     * Updates rider with the trips that he has completed / withdrawn / in-progress
     */
    public void updateTripForRider(final int riderId, Trip trip) {
        if (!riders.containsKey(riderId)) {
            throw new RiderNotFoundException("Rider with rider Id = " + riderId + " not found.");
        }

        riders.get(riderId).getTrips().add(trip);
    }

    /**
     * Method to get all trips done by a particular rider.
     *
     * @param rider Object.
     * @return List of Trip.
     */
    public List<Trip> tripHistory(Rider rider, String password) {
        if (rider.getPassword().equals(password)) {
            return rider.getTrips();
        }
        else {
            throw new InvalidPasswordException("The password for the rider is incorrect!");
        }
    }

    /** trip history function called by internal APIs, without any password check */
    public List<Trip> tripHistoryInternal(Rider rider) {
        return rider.getTrips();
    }
}
