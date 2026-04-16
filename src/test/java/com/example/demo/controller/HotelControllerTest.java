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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.verify;


@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

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

        when(hotelService.addAmenities(eq(1L), anyList())).thenReturn(expectedDto);

        mockMvc.perform(post("/api/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amenities", hasSize(2)))
                .andExpect(jsonPath("$.amenities[1]", is("pool")));
    }

    @Test void search_returnsHotelsMatchingName() throws Exception { 
      HotelShortDto dto = new HotelShortDto(); 
      dto.setId(5L); dto.setName("Alpha Hotel"); 
      when(hotelService.search("Alpha", null, null, null, null)).thenReturn(List.of(dto));

    mockMvc.perform(get("/api/hotels/search?name=Alpha"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is("Alpha Hotel")));
    }

    @Test void histogram_returnsCorrectCounts() throws Exception { 
      Map<String, Long> expected = Map.of( "Poland", 5L, "Germany", 3L ); 
      when(hotelService.histogram("country")).thenReturn(expected);

    mockMvc.perform(get("/api/hotels/histogram/country"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Poland", is(5)))
            .andExpect(jsonPath("$.Germany", is(3)));
    }

    @Test
    void delete_removesHotelAndReturnsNoContent() throws Exception {
        // doNothing() не обязателен, Mockito по умолчанию не бросает исключений для void
        mockMvc.perform(delete("/api/hotels/1"))
                .andExpect(status().isNoContent());

        verify(hotelService).delete(1L);
    }

}
