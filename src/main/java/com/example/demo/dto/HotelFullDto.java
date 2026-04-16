package com.example.demo.dto;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class HotelFullDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 500, message = "Description too long")
    private String description;

    private String brand;

    private AddressDto address;
    private ContactsDto contacts;
    private ArrivalTimeDto arrivalTime;

    private List<String> amenities;
}
