package com.TicketSmart.ticketsmart.dto;

import com.TicketSmart.ticketsmart.entity.UserType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long id;
	private String name;
	private String email;
	private String password;
	private String phoneNumber;
	private UserType type;

	public UserDTO(Long id, String name, String email, String phoneNumber, UserType type) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.type = type;

	}
}
