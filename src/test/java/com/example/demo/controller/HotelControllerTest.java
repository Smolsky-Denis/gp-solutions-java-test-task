package com.example.demo.controller;

import com.example.demo.dto.HotelFullDto;
import com.example.demo.dto.HotelShortDto;
import com.example.demo.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsListOfHotels() throws Exception {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(1L);
        dto.setName("Test Hotel");

        when(hotelService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Hotel")));
    }

    @Test
    void getById_returnsHotel() throws Exception {
        HotelFullDto dto = new HotelFullDto();
        dto.setId(1L);
        dto.setName("Test Hotel");
        dto.setDescription("Desc");

        when(hotelService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Hotel")))
                .andExpect(jsonPath("$.description", is("Desc")));
    }

    @Test
    void create_returnsCreatedHotel() throws Exception {
        HotelFullDto dto = new HotelFullDto();
        dto.setName("New Hotel");
        dto.setDescription("Desc");
        dto.setAmenities(List.of("wifi"));

        HotelShortDto responseDto = new HotelShortDto();
        responseDto.setId(10L);
        responseDto.setName("New Hotel");

        when(hotelService.create(any(HotelFullDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("New Hotel")));
    }

    @Test
    void addAmenities_returnsUpdatedHotel() throws Exception {
        List<String> amenitiesToAdd = List.of("pool");
        String requestBody = objectMapper.writeValueAsString(amenitiesToAdd);

        HotelFullDto expectedDto = new HotelFullDto();
        expectedDto.setId(1L);
        expectedDto.setName("Test Hotel");
        expectedDto.setAmenities(List.of("wifi", "pool"));

        when(hotelService.addAmenities(eq(1L), anyList()))
                .thenReturn(expectedDto);

        mockMvc.perform(post("/api/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amenities", hasSize(2)))
                .andExpect(jsonPath("$.amenities[1]", is("pool")));
    }

    @Test
    void search_returnsHotelsMatchingName() throws Exception {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(5L);
        dto.setName("Alpha Hotel");

        when(hotelService.search("Alpha", null, null, null, null))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/hotels/search?name=Alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alpha Hotel")));
    }

    @Test
    void histogram_returnsCorrectCounts() throws Exception {
        Map<String, Long> expected = Map.of(
                "Poland", 5L,
                "Germany", 3L
        );

        when(hotelService.histogram("country")).thenReturn(expected);

        mockMvc.perform(get("/api/hotels/histogram/country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Poland", is(5)))
                .andExpect(jsonPath("$.Germany", is(3)));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/hotels/1"))
                .andExpect(status().isNoContent());

        verify(hotelService).delete(1L);
    }

    // -------------------------
    // NEGATIVE TESTS
    // -------------------------

    @Test
    void getById_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/hotels/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void create_withEmptyBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void histogram_unknownType_returnsBadRequest() throws Exception {
        when(hotelService.histogram("unknown"))
                .thenThrow(new RuntimeException("Invalid histogram parameter"));

        mockMvc.perform(get("/api/hotels/histogram/unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getById_notFound_returnsNotFound() throws Exception {
        when(hotelService.getById(99L))
                .thenThrow(new RuntimeException("Hotel not found"));

        mockMvc.perform(get("/api/hotels/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void addAmenities_hotelNotFound_returnsNotFound() throws Exception {
        List<String> amenities = List.of("pool");
        String requestBody = objectMapper.writeValueAsString(amenities);

        when(hotelService.addAmenities(eq(99L), anyList()))
                .thenThrow(new RuntimeException("Hotel not found"));

        mockMvc.perform(post("/api/hotels/99/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void delete_hotelNotFound_returnsNotFound() throws Exception {
        doThrow(new RuntimeException("Hotel not found"))
                .when(hotelService).delete(99L);

        mockMvc.perform(delete("/api/hotels/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void addAmenities_withEmptyBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}