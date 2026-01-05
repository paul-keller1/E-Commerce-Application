package com.app.service;

import java.util.List;

import com.app.exception.APIException;
import com.app.model.Address;
import com.app.dto.AddressDTO;

public interface AddressService {


	/*@
		public normal_behavior
		  requires addressDTO != null;
		  requires addressDTO.getCountry() != null;
		  requires addressDTO.getState() != null;
		  requires addressDTO.getCity() != null;
		  requires addressDTO.getPincode() != null;
		  requires addressDTO.getStreet() != null;
		  requires addressDTO.getBuildingName() != null;
		  ensures \result != null;


		  also


		 public exceptional_behavior
			requires addressDTO != null;
			requires addressDTO.getCountry() != null;
			requires addressDTO.getState() != null;
			requires addressDTO.getCity() != null;
			requires addressDTO.getPincode() != null;
			requires addressDTO.getStreet() != null;
			requires addressDTO.getBuildingName() != null;
    		signals (APIException e) true;

	 @*/
	AddressDTO createAddress(AddressDTO addressDTO) throws APIException;




	/*@
		public normal_behavior
		  ensures \result != null;



	@*/
	List<AddressDTO> getAddresses();


	/*@
    public normal_behavior
      requires addressId != null;
      ensures \result != null;

   also

	 public exceptional_behavior
		requires addressId != null;
      	signals (APIException e) true;

  	@*/
	AddressDTO getAddress(Long addressId) throws APIException;



	/*@
    public normal_behavior
      requires addressId != null;
      requires address != null;
      requires address.getCountry() != null;
      requires address.getState() != null;
      requires address.getCity() != null;
      requires address.getPincode() != null;
      requires address.getStreet() != null;
      requires address.getBuildingName() != null;
      ensures \result != null;

     also

     public exceptional_behavior
      requires addressId != null;
      requires address != null;
      requires address.getCountry() != null;
      requires address.getState() != null;
      requires address.getCity() != null;
      requires address.getPincode() != null;
      requires address.getStreet() != null;
      requires address.getBuildingName() != null;
      signals (APIException e) true;
  	@*/
	AddressDTO updateAddress(Long addressId, Address address) throws APIException;



	/*@
	public normal_behavior
	  requires addressId != null;
	  ensures \result != null;


	also


	 public exceptional_behavior
      requires addressId != null;
      signals (APIException e) true;
	@*/
	String deleteAddress(Long addressId) throws APIException;
}
