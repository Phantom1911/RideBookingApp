import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.company.exception.CarAlreadyPresentException;
import com.company.exception.DriverAlreadyPresentException;
import com.company.exception.DriverNotFoundException;
import com.company.manager.DriverManager;
import com.company.model.Car;
import com.company.model.CarType;
import com.company.model.Driver;
import com.company.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DriverManagerTest {

	DriverManager driverManager;

	@BeforeEach
	void setup() {
		driverManager = new DriverManager();
	}

	@Test
	void test_createDriverAndGetDrivers() {
        /**
         * Each driver is uniquely identified by the driver id, which is int
         * driver registers with name, id, password and the Car that he would be driving
         * after registration the driver can make himself available to take the trips
         */
		// Given.
        // a check on the validity of registration number can be added as well by creating CarManager which has all the cars and checks if this regtn number is already present with us
        Car car1 = new Car(CarType.HATCHBACK, "ABCD-12");
        Car car2 = new Car(CarType.SEDAN, "ABCD-34");
        Car car3 = new Car(CarType.HATCHBACK, "ABCD-56");
        Car car4 = new Car(CarType.HATCHBACK, "ABCD-78");


		Driver dummyDriver = new Driver(2, "Aastik", "abcd", car1);
        Driver dummyDriver2 = new Driver(4, "Sumit", "abcd", car2);

        Driver driver1 = new Driver(1, "Prashant", "abcd", car2);
        Driver driver2 = new Driver(2, "Prateek", "abcd", car3);
        Driver driver3 = new Driver(3, "Rajat", "abcd", car4);
        // Given.
        driverManager.createDriver(driver1);
        driverManager.createDriver(driver2);
        driverManager.createDriver(driver3);

        // the drivers have to make themselves available after registration
        driverManager.makeAvailable(1, new Location(2,3));
        driverManager.makeAvailable(2, new Location(2,3));
        driverManager.makeAvailable(3, new Location(2,3));

        // Then.
        // when we try to create driver with an id that's already present
		assertThrows(DriverAlreadyPresentException.class, () -> {
			// When.
			driverManager.createDriver(dummyDriver);
		});

        // when we try to create driver with a car that's already registered with us
        assertThrows(CarAlreadyPresentException.class, () -> {
            // When.
            driverManager.createDriver(dummyDriver2);
        });

		// Then.
		assertEquals(3, driverManager.getDrivers().size());
	}

	@Test
	void test_updateDriverAvailability() {

        Car car2 = new Car(CarType.SEDAN, "ABCD-34");
        Car car3 = new Car(CarType.HATCHBACK, "ABCD-56");
        Car car4 = new Car(CarType.HATCHBACK, "ABCD-78");

        Driver driver1 = new Driver(1, "Prashant", "abcd", car2);
        Driver driver2 = new Driver(2, "Prateek", "abcd", car3);
        Driver driver3 = new Driver(3, "Rajat", "abcd", car4);
		// Given.
        driverManager.createDriver(driver1);
        driverManager.createDriver(driver2);
        driverManager.createDriver(driver3);

        // the drivers have to make themselves available after registration
        driverManager.makeAvailable(1, new Location(2,3));
        driverManager.makeAvailable(2, new Location(2,3));
        driverManager.makeAvailable(3, new Location(2,3));

		assertEquals(3, driverManager.getDrivers().size());

		// When.
		driverManager.updateDriverAvailability(3, false);

		// Then.
		assertEquals(2, driverManager.getDrivers().size());

		// Then.
		assertThrows(DriverNotFoundException.class, () -> {
			// When.
			driverManager.updateDriverAvailability(10, false);
		});
	}
}
