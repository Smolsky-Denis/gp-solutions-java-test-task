package com.example.demo.dto;

import lombok.Data;

@Data
public class HotelShortDto {

    private Long id;
    private String name;
    private String description;
    private String address; // one string representation of address
    private String phone;
}