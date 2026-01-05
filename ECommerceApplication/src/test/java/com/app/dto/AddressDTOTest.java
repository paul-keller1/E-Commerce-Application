package com.app.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressDTOTest {

    @Test
    void noArgsConstructorAndSettersShouldWork() {
        AddressDTO dto = new AddressDTO();

        dto.setAddressId(1L);
        dto.setStreet("Main Street");
        dto.setBuildingName("Sunrise Apartments");
        dto.setCity("Berlin");
        dto.setState("Berlin");
        dto.setCountry("Germany");
        dto.setPincode("10115");

        assertEquals(1L, dto.getAddressId());
        assertEquals("Main Street", dto.getStreet());
        assertEquals("Sunrise Apartments", dto.getBuildingName());
        assertEquals("Berlin", dto.getCity());
        assertEquals("Berlin", dto.getState());
        assertEquals("Germany", dto.getCountry());
        assertEquals("10115", dto.getPincode());
    }

    @Test
    void allArgsConstructorShouldSetAllFields() {
        AddressDTO dto = new AddressDTO(
                1L,
                "Main Street",
                "Sunrise Apartments",
                "Berlin",
                "Berlin",
                "Germany",
                "10115"
        );

        assertEquals(1L, dto.getAddressId());
        assertEquals("Main Street", dto.getStreet());
        assertEquals("Sunrise Apartments", dto.getBuildingName());
        assertEquals("Berlin", dto.getCity());
        assertEquals("Berlin", dto.getState());
        assertEquals("Germany", dto.getCountry());
        assertEquals("10115", dto.getPincode());
    }

    @Test
    void equalsAndHashCodeShouldWorkForIdenticalObjects() {
        AddressDTO a1 = new AddressDTO(
                1L,
                "Main Street",
                "Sunrise Apartments",
                "Berlin",
                "Berlin",
                "Germany",
                "10115"
        );

        AddressDTO a2 = new AddressDTO(
                1L,
                "Main Street",
                "Sunrise Apartments",
                "Berlin",
                "Berlin",
                "Germany",
                "10115"
        );

        AddressDTO a3 = new AddressDTO(
                2L,
                "Other Street",
                "Moonlight House",
                "Munich",
                "Bavaria",
                "Germany",
                "80000"
        );

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, a3);
    }

    @Test
    void equalsShouldHandleNullAndDifferentType() {
        AddressDTO dto = new AddressDTO(
                1L,
                "Main Street",
                "Sunrise Apartments",
                "Berlin",
                "Berlin",
                "Germany",
                "10115"
        );

        assertEquals(dto, dto);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    @Test
    void toStringShouldContainClassNameAndFields() {
        AddressDTO dto = new AddressDTO(
                1L,
                "Main Street",
                "Sunrise Apartments",
                "Berlin",
                "Berlin",
                "Germany",
                "10115"
        );

        String s = dto.toString();

        assertNotNull(s);
        assertTrue(s.contains("AddressDTO"));
        assertTrue(s.contains("Main Street"));
        assertTrue(s.contains("Berlin"));
    }
}
