package com.example.demo.service;

import com.example.demo.dto.HotelFullDto;
import com.example.demo.dto.HotelShortDto;
import com.example.demo.entity.Address;
import com.example.demo.entity.Contacts;
import com.example.demo.entity.Hotel;
import com.example.demo.repository.HotelRepository;
import com.example.demo.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HotelServiceImplTest {

    private HotelRepository hotelRepository;
    private HotelServiceImpl hotelServiceImpl;

    @BeforeEach
    void setUp() {
        hotelRepository = Mockito.mock(HotelRepository.class);
        hotelServiceImpl = new HotelServiceImpl(hotelRepository);

        // 🔥 FIX: save should return same object
        when(hotelRepository.save(any(Hotel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getAll_returnsListOfHotelShortDtos() {
        Hotel testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");
        testHotel.setDescription("Desc");
        testHotel.setAddress(new Address());
        testHotel.setContacts(new Contacts());
        testHotel.setAmenities(List.of("wifi"));

        when(hotelRepository.findAll()).thenReturn(List.of(testHotel));

        List<HotelShortDto> result = hotelServiceImpl.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getName());
    }

    @Test
    void getById_returnsHotelFullDto() {
        Hotel testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");
        testHotel.setDescription("Test Description");
        testHotel.setAddress(new Address());
        testHotel.setContacts(new Contacts());
        testHotel.setAmenities(List.of("WiFi", "Pool"));

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        HotelFullDto result = hotelServiceImpl.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Hotel", result.getName());
        assertFalse(result.getAmenities().isEmpty());
    }

    @Test
    void search_returnsHotelsMatchingName() {
        Hotel alpha = new Hotel();
        alpha.setName("Alpha Hotel");

        Hotel beta = new Hotel();
        beta.setName("Beta Hotel");

        when(hotelRepository.findAll()).thenReturn(List.of(alpha, beta));

        List<HotelShortDto> result = hotelServiceImpl.search("Alpha", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Alpha Hotel", result.get(0).getName());
    }

    @Test
    void create_returnsHotelShortDtoWithId() {
        HotelFullDto dto = new HotelFullDto();
        dto.setName("New Hotel");
        dto.setDescription("Desc");
        dto.setAmenities(List.of("wifi"));

        Hotel testHotel = new Hotel();
        testHotel.setId(10L);
        testHotel.setName("New Hotel");
        testHotel.setDescription("Desc");
        testHotel.setAmenities(List.of("wifi"));

        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        HotelShortDto result = hotelServiceImpl.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("New Hotel", result.getName());
    }

    @Test
    void addAmenities_addsAmenitiesToHotel() {
        Hotel testHotel = new Hotel();
        testHotel.setId(1L);

        List<String> initialAmenities = new ArrayList<>();
        initialAmenities.add("wifi");

        testHotel.setAmenities(initialAmenities);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        hotelServiceImpl.addAmenities(1L, List.of("pool"));

        verify(hotelRepository).save(testHotel);
        assertEquals(2, testHotel.getAmenities().size());
        assertTrue(testHotel.getAmenities().contains("pool"));
    }
}