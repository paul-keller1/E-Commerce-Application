package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.app.dto.AddressDTO;
import com.app.model.Address;
import com.app.service.AddressService;
import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressDTO = new AddressDTO(1L, "Street", "Building", "City", "State", "Country", "00001");
    }

    @Test
    void createAddress_ShouldReturnCreatedAddress() {
        when(addressService.createAddress(addressDTO)).thenReturn(addressDTO);

        ResponseEntity<AddressDTO> response = addressController.createAddress(addressDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(addressDTO, response.getBody());
    }

    @Test
    void getAddresses_ShouldReturnList() {
        when(addressService.getAddresses()).thenReturn(List.of(addressDTO));

        ResponseEntity<List<AddressDTO>> response = addressController.getAddresses();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAddress_ShouldReturnAddress() {
        when(addressService.getAddress(1L)).thenReturn(addressDTO);

        ResponseEntity<AddressDTO> response = addressController.getAddress(1L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(addressDTO, response.getBody());
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddress() {
        Address address = new Address();
        when(addressService.updateAddress(1L, address)).thenReturn(addressDTO);

        ResponseEntity<AddressDTO> response = addressController.updateAddress(1L, address);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addressDTO, response.getBody());
    }

    @Test
    void deleteAddress_ShouldReturnStatus() {
        when(addressService.deleteAddress(1L)).thenReturn("deleted");

        ResponseEntity<String> response = addressController.deleteAddress(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("deleted", response.getBody());
    }
}
