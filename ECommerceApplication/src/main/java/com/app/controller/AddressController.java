package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.model.Address;
import com.app.dto.AddressDTO;
import com.app.service.AddressService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "E-Commerce Application")
public class AddressController {

	@Autowired
	private AddressService addressService;

    /*@
      private invariant addressService != null;
    @*/


    /*@
      public normal_behavior
		  requires addressDTO != null;
		  ensures \result != null;
		  ensures \result.getStatusCodeValue() == 201;
		  ensures \result.getBody() != null;
    @*/
	@PostMapping("/address")
	public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
		AddressDTO savedAddressDTO = addressService.createAddress(addressDTO);
		return new ResponseEntity<AddressDTO>(savedAddressDTO, HttpStatus.CREATED);
	}

    /*@
      public normal_behavior
      ensures \result != null;
      ensures \result.getStatusCodeValue() == 302;
      ensures \result.getBody() != null;
    @*/
	@GetMapping("/addresses")
	public ResponseEntity<List<AddressDTO>> getAddresses() {
		List<AddressDTO> addressDTOs = addressService.getAddresses();
		return new ResponseEntity<List<AddressDTO>>(addressDTOs, HttpStatus.FOUND);
	}

    /*@
      public normal_behavior
      requires addressId != null;
      requires addressId.longValue() > 0;
      ensures \result != null;
      ensures \result.getStatusCodeValue() == 302;
      ensures \result.getBody() != null;
    @*/
	@GetMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId) {
		AddressDTO addressDTO = addressService.getAddress(addressId);
		return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.FOUND);
	}

    /*@
      public normal_behavior
      requires addressId != null;
      requires addressId.longValue() > 0;
      requires address != null;
      ensures \result != null;
      ensures \result.getStatusCodeValue() == 200;
      ensures \result.getBody() != null;
    @*/
	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,
													@RequestBody Address address) {
		AddressDTO addressDTO = addressService.updateAddress(addressId, address);
		return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.OK);
	}

    /*@
      public normal_behavior
      requires addressId != null;
      requires addressId.longValue() > 0;
      ensures \result != null;
      ensures \result.getStatusCodeValue() == 200;
      ensures \result.getBody() != null;
    @*/
	@DeleteMapping("/addresses/{addressId}")
	public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
		String status = addressService.deleteAddress(addressId);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
