package com.example.demo.controller;

import com.example.demo.dto.HotelFullDto;
import com.example.demo.dto.HotelShortDto;
import com.example.demo.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/hotels")
    public List<HotelShortDto> getAll() {
        return hotelService.getAll();
    }

    @GetMapping("/hotels/{id}")
    public HotelFullDto getById(@PathVariable Long id) {
        return hotelService.getById(id);
    }

    @GetMapping("/hotels/search")
    public List<HotelShortDto> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities) {

        return hotelService.search(name, brand, city, country, amenities);
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelShortDto> create(@RequestBody HotelFullDto dto) {
        return ResponseEntity.status(201).body(hotelService.create(dto));
    }

    @PostMapping("/hotels/{id}/amenities")
    public ResponseEntity<HotelFullDto> addAmenities(
            @PathVariable Long id,
            @RequestBody List<String> amenities) {
        return ResponseEntity.ok(hotelService.addAmenities(id, amenities));
    }

    @GetMapping("/hotels/histogram/{param}")
    public Map<String, Long> histogram(@PathVariable String param) {
        return hotelService.histogram(param);
    }

    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}