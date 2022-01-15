package com.company.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Rider {
    int id;
    String name;
    List<Trip> trips;
    String password;

    public Rider(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.trips = new ArrayList<>();
    }
}
