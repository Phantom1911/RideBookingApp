package com.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Car {
    private  CarType carType;
    private String registrationNumber;

}
