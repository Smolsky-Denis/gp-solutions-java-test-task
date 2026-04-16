package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.HotelFullDto;
import com.example.demo.dto.HotelShortDto;

public interface HotelService {

    List<HotelShortDto> getAll();

    HotelFullDto getById(Long id);

    List<HotelShortDto> search(
            String name,
            String brand,
            String city,
            String country,
            List<String> amenities
    );

    HotelShortDto create(HotelFullDto dto);

    HotelFullDto addAmenities(Long id, List<String> amenities);

    Map<String, Long> histogram(String param);

    void delete(Long id);
}