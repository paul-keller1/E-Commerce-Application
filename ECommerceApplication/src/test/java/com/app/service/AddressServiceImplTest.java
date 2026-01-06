package com.app.service;

import com.app.dto.AddressDTO;
import com.app.exception.APIException;
import com.app.model.Address;
import com.app.model.User;
import com.app.repository.AddressRepo;
import com.app.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @Mock
    private AddressRepo addressRepo;

    @Mock
    private UserRepo userRepo;

    private ModelMapper modelMapper;

    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
        addressService = new AddressServiceImpl();
        ReflectionTestUtils.setField(addressService, "addressRepo", addressRepo);
        ReflectionTestUtils.setField(addressService, "userRepo", userRepo);
        ReflectionTestUtils.setField(addressService, "modelMapper", modelMapper);
    }

    private AddressDTO buildAddressDTO() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(1L);
        dto.setCountry("Country");
        dto.setState("State");
        dto.setCity("City");
        dto.setPincode("12345");
        dto.setStreet("Street");
        dto.setBuildingName("Building");
        return dto;
    }

    private Address buildAddress() {
        Address a = new Address();
        a.setAddressId(1L);
        a.setCountry("Country");
        a.setState("State");
        a.setCity("City");
        a.setPincode("12345");
        a.setStreet("Street");
        a.setBuildingName("Building");
        return a;
    }

    @Test
    void createAddress_WhenNotExists_ShouldSaveAndReturnDto() {
        AddressDTO dto = buildAddressDTO();

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(null);

        Address toSave = buildAddress();
        when(addressRepo.save(any(Address.class))).thenReturn(toSave);

        AddressDTO result = addressService.createAddress(dto);

        assertEquals(dto.getCountry(), result.getCountry());
        assertEquals(dto.getCity(), result.getCity());
        verify(addressRepo).save(any(Address.class));
    }

    @Test
    void createAddress_WhenExists_ShouldThrowApiException() {
        AddressDTO dto = buildAddressDTO();
        Address existing = buildAddress();

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(existing);

        assertThrows(APIException.class, () -> addressService.createAddress(dto));
        verify(addressRepo, never()).save(any());
    }

    @Test
    void getAddresses_ShouldReturnMappedDtos() {
        Address a1 = buildAddress();
        Address a2 = buildAddress();
        a2.setAddressId(2L);
        a2.setCity("Other");

        when(addressRepo.findAll()).thenReturn(List.of(a1, a2));

        List<AddressDTO> result = addressService.getAddresses();

        assertEquals(2, result.size());
        assertEquals(a1.getCity(), result.get(0).getCity());
        assertEquals(a2.getCity(), result.get(1).getCity());
    }

    @Test
    void getAddress_WhenExists_ShouldReturnDto() {
        Address address = buildAddress();
        when(addressRepo.findById(1L)).thenReturn(Optional.of(address));

        AddressDTO result = addressService.getAddress(1L);

        assertEquals(address.getAddressId(), result.getAddressId());
        assertEquals(address.getCity(), result.getCity());
    }

    @Test
    void getAddress_WhenNotFound_ShouldThrowApiException() {
        when(addressRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> addressService.getAddress(1L));
    }
    @Test
    void updateAddress_WhenNoMatchingAddress_ShouldUpdateExisting() {
        Address existing = buildAddress();
        existing.setAddressId(1L);

        Address newData = new Address();
        newData.setCountry("NewCountry");
        newData.setState("NewState");
        newData.setCity("NewCity");
        newData.setPincode("99999");
        newData.setStreet("NewStreet");
        newData.setBuildingName("NewBuilding");

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(null);

        when(addressRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(addressRepo.save(existing)).thenReturn(existing);

        AddressDTO result = addressService.updateAddress(1L, newData);

        // Assert every updated attribute (kills "" mutants in getters used for mapping)
        assertEquals("NewCountry", result.getCountry());
        assertEquals("NewState", result.getState());
        assertEquals("NewCity", result.getCity());
        assertEquals("99999", result.getPincode());
        assertEquals("NewStreet", result.getStreet());
        assertEquals("NewBuilding", result.getBuildingName());

        verify(addressRepo).save(existing);
    }


    @Test
    void updateAddress_WhenMatchingAddressExists_ShouldAttachToUsersAndDeleteOld() {
        Address matching = buildAddress();
        matching.setAddressId(2L);
        matching.setCountry("MatchCountry");
        matching.setState("MatchState");
        matching.setCity("MatchCity");
        matching.setPincode("22222");
        matching.setStreet("MatchStreet");
        matching.setBuildingName("MatchBuilding");

        Address oldAddress = buildAddress();
        oldAddress.setAddressId(1L);

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(matching);

        User user = new User();
        user.setAddresses(new ArrayList<>());

        when(userRepo.findByAddress(1L)).thenReturn(List.of(user));
        when(addressRepo.findById(1L)).thenReturn(Optional.of(oldAddress));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AddressDTO result = addressService.updateAddress(1L, matching);

        // Assert mapping is correct for *all* relevant fields
        assertEquals("MatchCountry", result.getCountry());
        assertEquals("MatchState", result.getState());
        assertEquals("MatchCity", result.getCity());
        assertEquals("22222", result.getPincode());
        assertEquals("MatchStreet", result.getStreet());
        assertEquals("MatchBuilding", result.getBuildingName());

        verify(addressRepo).deleteById(1L);
        verify(userRepo, atLeastOnce()).save(user);
        assertTrue(user.getAddresses().contains(matching));
    }


    @Test
    void deleteAddress_WhenExists_ShouldRemoveFromUsersAndDelete() {
        Address address = buildAddress();

        when(addressRepo.findById(1L)).thenReturn(Optional.of(address));

        User user = new User();
        user.setAddresses(new ArrayList<>());
        user.getAddresses().add(address);

        when(userRepo.findByAddress(1L)).thenReturn(List.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = addressService.deleteAddress(1L);

        assertTrue(result.contains("1"));
        verify(addressRepo).deleteById(1L);
        verify(userRepo, atLeastOnce()).save(user);
        assertFalse(user.getAddresses().contains(address));
    }

    @Test
    void deleteAddress_WhenNotFound_ShouldThrowApiException() {
        when(addressRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> addressService.deleteAddress(1L));
    }

    @Test
    void updateAddress_AddressNotFound_ThrowsAPIException() {
        Long addressId = 1L;

        Address address = new Address();
        address.setCountry("Country");
        address.setState("State");
        address.setCity("City");
        address.setPincode("12345");
        address.setStreet("Street");
        address.setBuildingName("Building");

        // 1) No existing address with same full data (first query returns null)
        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(null);

        // 2) No address by ID either → triggers orElseThrow
        when(addressRepo.findById(addressId)).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> addressService.updateAddress(addressId, address)
        );

        assertTrue(ex.getMessage().contains("Address with addressId " + addressId + " not found"));

        verify(addressRepo).findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(addressRepo).findById(addressId);
        verify(addressRepo, never()).save(any(Address.class));
    }
}
