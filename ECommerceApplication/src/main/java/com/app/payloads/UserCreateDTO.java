package com.app.payloads;

import java.util.Set;

import com.app.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String password;
    private AddressDTO address;
}

