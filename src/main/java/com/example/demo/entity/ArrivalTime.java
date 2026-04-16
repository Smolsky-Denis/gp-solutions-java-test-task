package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ArrivalTime {

    private String checkIn;
    private String checkOut;
}