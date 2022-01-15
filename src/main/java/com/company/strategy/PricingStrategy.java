package com.company.strategy;

import com.company.model.CarType;
import com.company.model.Location;

public interface PricingStrategy {

    Integer AMT_PER_KM = 20;

    double calculateFare(CarType carType, final Location origin, final Location destination, DistanceFindingStrategy distanceFindingStrategy);

    double calculateFareForPreferred(CarType carType, final Location origin, final Location destination,
            DistanceFindingStrategy distanceFindingStrategy);
}
