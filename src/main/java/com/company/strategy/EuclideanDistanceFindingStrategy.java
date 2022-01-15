package com.company.strategy;

import com.company.model.Location;

public class EuclideanDistanceFindingStrategy implements DistanceFindingStrategy {

    @Override
    public Double distanceBetweenLocations(Location location1, Location location2) {
        int lat1 = location1.getLatitude();
        int long1 = location1.getLongitude();
        int lat2 = location2.getLatitude();
        int long2 = location2.getLongitude();

        return Math.sqrt(Math.pow((lat1-lat2), 2) + Math.pow((long1-long2), 2));
    }
}
