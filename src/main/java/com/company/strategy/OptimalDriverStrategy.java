package com.company.strategy;

import com.company.model.CarType;
import com.company.model.Driver;
import com.company.model.Location;
import com.company.model.Rider;
import java.util.List;
import java.util.Optional;

public class OptimalDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Optional<Driver> findDriver(Rider rider, List<Driver> nearByDrivers, Location origin, Location destination,
            CarType requestedCarType) {
        for (Driver driver : nearByDrivers) {
            if (driver.getCar().getCarType() == requestedCarType && driver.isAvailable()) {
                return Optional.of(driver);
            }
        }

        // consider for auto-upgrade from hatchback to sedan if requested was hatchback
        if (requestedCarType == CarType.HATCHBACK) {
            for (Driver driver : nearByDrivers) {
                // if a car of type SEDAN is available, we upgrade the rider
                if (driver.getCar().getCarType() == CarType.SEDAN) {
                    return Optional.of(driver);
                }
            }
        }
        return Optional.empty();
    }
}
