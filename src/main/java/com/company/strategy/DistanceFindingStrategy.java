package com.company.strategy;

import com.company.model.Location;

public interface DistanceFindingStrategy {

    /**
     * Given the two locations, find the distance between them in standard unit
     */
    Double distanceBetweenLocations(Location location1, Location location2);

}
