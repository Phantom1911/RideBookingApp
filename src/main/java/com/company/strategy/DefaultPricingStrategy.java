package com.company.strategy;

import com.company.exception.InvalidCarTypeException;
import com.company.model.CarType;
import com.company.model.Location;


public class DefaultPricingStrategy implements PricingStrategy {

    private static final Double MIN_PRICE = 50.0;
    private static final Double PART_ONE_PRICE_PER_KM_HATCHBACK = 10.0;
    private static final Double PART_TWO_PRICE_PER_KM_HATCHBACK = 8.0;
    private static final Double PART_THREE_PRICE_PER_KM_HATCHBACK = 5.0;
    private static final Double PART_ONE_PRICE_PER_KM_SEDAN = 15.0;
    private static final Double PART_TWO_PRICE_PER_KM_SEDAN = 20.0;
    private static final Double PART_THREE_PRICE_PER_KM_SEDAN = 30.0;

  @Override
  public double calculateFare(CarType carType, final Location origin,
          final Location destination, DistanceFindingStrategy distanceFindingStrategy) {
      Double distance = distanceFindingStrategy.distanceBetweenLocations(origin, destination);
    // automatic upgrade is handled in this by itself, since request parameter takes in original car type requested
      double fare = 0D;
    switch (carType) {
        case HATCHBACK:
            if (distance >= 0 && distance <= 2) {
                fare += PART_ONE_PRICE_PER_KM_HATCHBACK * distance;
                fare = Math.max(fare, MIN_PRICE);
            } else if (distance > 2 && distance <= 5) {
                fare += PART_ONE_PRICE_PER_KM_HATCHBACK * 2 + PART_TWO_PRICE_PER_KM_HATCHBACK * (distance - 2);
                fare = Math.max(fare, MIN_PRICE);
            } else {
                fare += PART_ONE_PRICE_PER_KM_HATCHBACK * 2 + PART_TWO_PRICE_PER_KM_HATCHBACK * 3 + PART_THREE_PRICE_PER_KM_HATCHBACK * (distance - 5);
                fare = Math.max(fare, MIN_PRICE);
            }
            break;
        case SEDAN:
            if (distance >= 0 && distance <= 2) {
                fare += PART_ONE_PRICE_PER_KM_SEDAN * distance;
                fare = Math.max(fare, MIN_PRICE);
            } else if (distance > 2 && distance <= 5) {
                fare += PART_ONE_PRICE_PER_KM_SEDAN * 2 + PART_TWO_PRICE_PER_KM_SEDAN * (distance - 2);
                fare = Math.max(fare, MIN_PRICE);
            } else {
                fare += PART_ONE_PRICE_PER_KM_SEDAN * 2 + PART_TWO_PRICE_PER_KM_SEDAN * 3 + PART_THREE_PRICE_PER_KM_SEDAN * (distance - 5);
                fare = Math.max(fare, MIN_PRICE);
            }
            break;
        default:
            throw new InvalidCarTypeException("Car type is invalid!");
    }

    return fare;
  }

  // currently fare calculation for preferred rider is same as normal rider, but can be changed easily by changing below method.
  @Override
  public double calculateFareForPreferred(CarType carType, final Location origin,
          final Location destination, DistanceFindingStrategy distanceFindingStrategy) {
      Double distance = distanceFindingStrategy.distanceBetweenLocations(origin, destination);
      // automatic upgrade is handled in this by itself, since request parameter takes in original car type requested
      double fare = 0D;
      switch (carType) {
          case HATCHBACK:
              if (distance >= 0 && distance <= 2) {
                  fare += PART_ONE_PRICE_PER_KM_HATCHBACK * distance;
                  fare = Math.max(fare, MIN_PRICE);
              } else if (distance > 2 && distance <= 5) {
                  fare += PART_ONE_PRICE_PER_KM_HATCHBACK * 2 + PART_TWO_PRICE_PER_KM_HATCHBACK * (distance - 2);
                  fare = Math.max(fare, MIN_PRICE);
              } else {
                  fare += PART_ONE_PRICE_PER_KM_HATCHBACK * 2 + PART_TWO_PRICE_PER_KM_HATCHBACK * 3 + PART_THREE_PRICE_PER_KM_HATCHBACK * (distance - 5);
                  fare = Math.max(fare, MIN_PRICE);
              }
              break;
          case SEDAN:
              if (distance >= 0 && distance <= 2) {
                  fare += PART_ONE_PRICE_PER_KM_SEDAN * distance;
                  fare = Math.max(fare, MIN_PRICE);
              } else if (distance > 2 && distance <= 5) {
                  fare += PART_ONE_PRICE_PER_KM_SEDAN * 2 + PART_TWO_PRICE_PER_KM_SEDAN * (distance - 2);
                  fare = Math.max(fare, MIN_PRICE);
              } else {
                  fare += PART_ONE_PRICE_PER_KM_SEDAN * 2 + PART_TWO_PRICE_PER_KM_SEDAN * 3 + PART_THREE_PRICE_PER_KM_SEDAN * (distance - 5);
                  fare = Math.max(fare, MIN_PRICE);
              }
              break;
          default:
              throw new InvalidCarTypeException("Car type is invalid!");
      }

      return fare;
  }
}
