package com.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Size(min = 1, max = 30, message = "First Name must be between 1 and 30 characters long")
	@Pattern(regexp = "^(?!.*\\d).+$", message = "First Name must not contain numbers or special characters")
	private String firstName;

	@Size(min = 1, max = 30, message = "Last Name must be between 1 and 30 characters long")
	@Pattern(regexp = "^(?!.*\\d).+$", message = "Last Name must not contain numbers ")
	private String lastName;

	@Size(min = 10, max = 15, message = "Mobile Number must be  10 to 15 digits long and can start with +")
	@Pattern(regexp = "^\\+?\\d*$", message = "Mobile Number must contain only Numbers")
	private String mobileNumber;

	@Email
	@Column(unique = true, nullable = false)
	private String email;

	private String password;


	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "user_role")
	@Enumerated(EnumType.STRING)
	private Set<Role> roles;


	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "user_address", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	private List<Address> addresses = new ArrayList<>();

	@OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private Cart cart;




	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.getUserId());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OrderItem other)) {
			return false;
		}
		return Objects.equals(this.getUserId(), other.getOrderItemId());
	}

	@Override
	public String toString() {
		return "id=" + userId;
	}




}
