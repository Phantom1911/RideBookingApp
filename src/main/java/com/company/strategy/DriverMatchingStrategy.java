package com.company.strategy;

import com.company.model.CarType;
import com.company.model.Driver;
import com.company.model.Location;
import com.company.model.Rider;
import java.util.List;
import java.util.Optional;

public interface DriverMatchingStrategy {

  Optional<Driver> findDriver(Rider rider, List<Driver> nearByDrivers, Location origin, Location destination, CarType requestedCarType);
}
