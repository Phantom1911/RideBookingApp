import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.company.exception.DriverNotFoundException;
import com.company.exception.InvalidRideParamException;
import com.company.exception.TripNotFoundException;
import com.company.exception.TripStatusException;
import com.company.manager.DriverManager;
import com.company.manager.RiderManager;
import com.company.manager.TripManager;
import com.company.model.Car;
import com.company.model.CarType;
import com.company.model.Driver;
import com.company.model.Location;
import com.company.model.Rider;
import com.company.model.Trip;
import com.company.model.TripStatus;
import com.company.strategy.DefaultPricingStrategy;
import com.company.strategy.EuclideanDistanceFindingStrategy;
import com.company.strategy.OptimalDriverStrategy;
import com.company.strategy.PricingStrategy;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripManagerTest {

  TripManager tripManager;
  RiderManager riderManager;
  Driver driver1, driver2;
  Rider rider1, rider2, rider3;
  Car car1, car2;

  @BeforeEach
  void setup() {

      Car car1 = new Car(CarType.HATCHBACK, "ABCD-12");
      Car car2 = new Car(CarType.SEDAN, "ABCD-34");

      driver1 = new Driver(1, "Aastik", "abcd", car1);
      driver2 = new Driver(2, "Sumit", "abcd", car2);

    DriverManager driverManager = new DriverManager();
    driverManager.createDriver(driver1);
    driverManager.createDriver(driver2);

      driverManager.makeAvailable(1, new Location(2,3));
      driverManager.makeAvailable(2, new Location(2,3));

    rider1 = new Rider(2, "Ayush", "abcd");
    rider2 = new Rider(3, "Aastik", "abcd");
    rider3 = new Rider(4, "Shubham", "abcd");

    RiderManager riderManager = new RiderManager();
    riderManager.createRider(rider1);
    riderManager.createRider(rider2);
    riderManager.createRider(rider3);

    tripManager =
        new TripManager(riderManager, driverManager);
  }

  @Test
  void test_createRideMethod() {

    // Given.
    // Driver1 booked by rider1, one driver left now.
      Location origin1 = new Location(2,3);
      Location destination1 = new Location(4,5);
    tripManager.createTrip(rider1, origin1, destination1, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
            new OptimalDriverStrategy(), CarType.HATCHBACK);

      Location origin2 = new Location(2,3);
      Location destination2 = new Location(4,5);
    // Driver2 booked by rider2, zero driver left now.
      tripManager.createTrip(rider2, origin2, destination2, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
              new OptimalDriverStrategy(), CarType.SEDAN);
      assertEquals(2, tripManager.getTrips().size());
    // Then.
      // no drivers left now - should give no driver left exception
    assertThrows(
        DriverNotFoundException.class,
        () -> {
          // When.
            tripManager.createTrip(rider3, origin2, destination2, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
                    new OptimalDriverStrategy(), CarType.SEDAN);
        });

    // try booking trip to a very far off destination
    Location dest = new Location(1000000, 1000000);
    assertThrows(
        InvalidRideParamException.class,
        () -> {
            tripManager.createTrip(rider3, origin2, dest, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
                    new OptimalDriverStrategy(), CarType.SEDAN);
        });
  }

  @Test
  void test_updateTripWithWrongTripId() {
    // Then.
      // try updating the trip with a trip id that doesn't exist
    assertThrows(
            TripNotFoundException.class,
        () -> {
          // When.
          tripManager.updateTrip("random-id",
                  new Location(2,3), new Location(5,6),
                  new EuclideanDistanceFindingStrategy(), new DefaultPricingStrategy());
        });
  }

  @Test
  void test_updateTripWhichIsAlreadyCompletedOrWithdrawn() {

    // Given.
      Location origin1 = new Location(2,3);
      Location destination1 = new Location(4,5);
      String tripId = tripManager.createTrip(rider1, origin1, destination1, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
              new OptimalDriverStrategy(), CarType.HATCHBACK);
      tripManager.startTrip(tripId);
    tripManager.endTrip(tripId);

    // try updating a trip which has already ended
    // Then.
    assertThrows(
        TripStatusException.class,
        () -> {
          // When.
            tripManager.updateTrip(tripId,
                    new Location(2,3), new Location(5,6),
                    new EuclideanDistanceFindingStrategy(), new DefaultPricingStrategy());
        });
  }

  @Test
  void test_updateTripWithInvalidTripId() {

    // Then.
    Assertions.assertThrows(
        TripNotFoundException.class,
        () -> {
          // When.
            tripManager.updateTrip("random-id",
                    new Location(2,3), new Location(5,6),
                    new EuclideanDistanceFindingStrategy(), new DefaultPricingStrategy());
        });
  }

  @Test
  void test_withdrawTrip() {

    // Given.
      Location origin1 = new Location(2,3);
      Location destination1 = new Location(4,5);
      String tripId = tripManager.createTrip(rider1, origin1, destination1, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
              new OptimalDriverStrategy(), CarType.HATCHBACK);

    // When.
      Trip currentTrip = null;
    tripManager.withdrawTrip(tripId);
      List<Trip> tripsForRider = tripManager.getRiderManager().tripHistoryInternal(rider1);
      for (Trip trip : tripsForRider) {
          if (trip.getId() == tripId) {
              currentTrip = trip;
          }
      }

    // the status of this trip should become withdrawn now
    // Then.
    assertEquals(TripStatus.WITHDRAWN, currentTrip.getStatus());
  }

  @Test
  void test_withdrawTripWhichIsAlreadyCompleted() {

      // Given.
      Location origin1 = new Location(2,3);
      Location destination1 = new Location(4,5);
      String tripId = tripManager.createTrip(rider1, origin1, destination1, new DefaultPricingStrategy(), new EuclideanDistanceFindingStrategy(),
              new OptimalDriverStrategy(), CarType.HATCHBACK);
      tripManager.startTrip(tripId);
      tripManager.endTrip(tripId);

    // Then.
    assertThrows(
        TripStatusException.class,
        () -> {
          // When.
          tripManager.withdrawTrip(tripId);
        });
  }
}
