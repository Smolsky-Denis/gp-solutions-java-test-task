package com.example.demo.service.impl;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.HotelRepository;
import com.example.demo.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public List<HotelShortDto> getAll() {
        return hotelRepository.findAll().stream()
                .map(this::mapToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public HotelFullDto getById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return mapToFullDto(hotel);
    }

    @Override
    public List<HotelShortDto> search(String name, String brand, String city, String country, List<String> amenities) {
        return hotelRepository.findAll().stream()
                .filter(hotel -> {
                    if (name != null && (hotel.getName() == null ||
                            !hotel.getName().toLowerCase().contains(name.toLowerCase()))) {
                        return false;
                    }
                    if (brand != null && (hotel.getBrand() == null ||
                            !hotel.getBrand().equalsIgnoreCase(brand))) {
                        return false;
                    }
                    if (city != null && (hotel.getAddress() == null ||
                            !city.equalsIgnoreCase(hotel.getAddress().getCity()))) {
                        return false;
                    }
                    if (country != null && (hotel.getAddress() == null ||
                            !country.equalsIgnoreCase(hotel.getAddress().getCountry()))) {
                        return false;
                    }
                    if (amenities != null && !amenities.isEmpty()) {
                        List<String> hotelAmenities = hotel.getAmenities();
                        if (hotelAmenities == null) return false;
                        for (String a : amenities) {
                            if (!hotelAmenities.contains(a)) return false;
                        }
                    }
                    return true;
                })
                .map(this::mapToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public HotelShortDto create(HotelFullDto dto) {
        Hotel hotel = mapToEntity(dto);
        Hotel saved = hotelRepository.save(hotel);
        return mapToShortDto(saved);
    }

    @Override
    public HotelFullDto addAmenities(Long id, List<String> amenities) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        if (hotel.getAmenities() == null) {
            hotel.setAmenities(amenities);
        } else {
            hotel.getAmenities().addAll(amenities);
        }

        Hotel saved = hotelRepository.save(hotel);

        if (saved == null) {
            saved = hotel;
        }
        
        return mapToFullDto(saved);
    }

    @Override
    public Map<String, Long> histogram(String param) {
        List<Hotel> hotels = hotelRepository.findAll();

        return switch (param.toLowerCase()) {
            case "brand" -> hotels.stream()
                    .collect(Collectors.groupingBy(Hotel::getBrand, Collectors.counting()));
            case "city" -> hotels.stream()
                    .collect(Collectors.groupingBy(h -> safeAddress(h).getCity(), Collectors.counting()));
            case "country" -> hotels.stream()
                    .collect(Collectors.groupingBy(h -> safeAddress(h).getCountry(), Collectors.counting()));
            case "amenities" -> hotels.stream()
                    .flatMap(h -> h.getAmenities() == null ? List.<String>of().stream() : h.getAmenities().stream())
                    .collect(Collectors.groupingBy(a -> a, Collectors.counting()));
            default -> throw new RuntimeException("Invalid histogram parameter");
        };
    }

    @Override
    public void delete(Long id) {
        hotelRepository.deleteById(id);
    }

    // -------------------- MAPPERS --------------------

    private HotelShortDto mapToShortDto(Hotel hotel) {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setAddress(buildAddressString(hotel.getAddress()));
        dto.setPhone(hotel.getContacts() != null ? hotel.getContacts().getPhone() : null);
        return dto;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safe(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Address safeAddress(Hotel h) {
        return h.getAddress() == null ? new Address() : h.getAddress();
    }

    private String buildAddressString(Address address) {
    if (address == null) return null;

    return String.format("%s %s, %s, %s, %s",
            safe(address.getHouseNumber()),
            safe(address.getStreet()),
            safe(address.getCity()),
            safe(address.getPostCode()),
            safe(address.getCountry()));
    }

    private HotelFullDto mapToFullDto(Hotel hotel) {
        HotelFullDto dto = new HotelFullDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setBrand(hotel.getBrand());

        dto.setAddress(mapAddressToDto(hotel.getAddress()));
        dto.setContacts(mapContactsToDto(hotel.getContacts()));
        dto.setArrivalTime(mapArrivalTimeToDto(hotel.getArrivalTime()));

        dto.setAmenities(hotel.getAmenities());
        return dto;
    }

    private AddressDto mapAddressToDto(Address address) {
        if (address == null) return null;
        AddressDto dto = new AddressDto();
        dto.setHouseNumber(address.getHouseNumber());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        dto.setPostCode(address.getPostCode());
        return dto;
    }

    private ContactsDto mapContactsToDto(Contacts contacts) {
        if (contacts == null) return null;
        ContactsDto dto = new ContactsDto();
        dto.setPhone(contacts.getPhone());
        dto.setEmail(contacts.getEmail());
        return dto;
    }

    private ArrivalTimeDto mapArrivalTimeToDto(ArrivalTime arrival) {
        if (arrival == null) return null;
        ArrivalTimeDto dto = new ArrivalTimeDto();
        dto.setCheckIn(arrival.getCheckIn());
        dto.setCheckOut(arrival.getCheckOut());
        return dto;
    }

    private Hotel mapToEntity(HotelFullDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setDescription(dto.getDescription());
        hotel.setBrand(dto.getBrand());

        hotel.setAddress(mapAddress(dto.getAddress()));
        hotel.setContacts(mapContacts(dto.getContacts()));
        hotel.setArrivalTime(mapArrivalTime(dto.getArrivalTime()));

        hotel.setAmenities(dto.getAmenities());
        return hotel;
    }

    private Address mapAddress(AddressDto dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setHouseNumber(dto.getHouseNumber());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPostCode(dto.getPostCode());
        return address;
    }

    private Contacts mapContacts(ContactsDto dto) {
        if (dto == null) return null;
        Contacts contacts = new Contacts();
        contacts.setPhone(dto.getPhone());
        contacts.setEmail(dto.getEmail());
        return contacts;
    }

    private ArrivalTime mapArrivalTime(ArrivalTimeDto dto) {
        if (dto == null) return null;
        ArrivalTime arrival = new ArrivalTime();
        arrival.setCheckIn(dto.getCheckIn());
        arrival.setCheckOut(dto.getCheckOut());
        return arrival;
    }
}
