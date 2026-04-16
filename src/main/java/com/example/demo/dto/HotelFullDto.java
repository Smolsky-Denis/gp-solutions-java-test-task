package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
public class HotelFullDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 500, message = "Description too long")
    private String description;

    private String brand;

    @Valid
    private AddressDto address;

    @Valid
    private ContactsDto contacts;

    @Valid
    private ArrivalTimeDto arrivalTime;

    private List<String> amenities;
}