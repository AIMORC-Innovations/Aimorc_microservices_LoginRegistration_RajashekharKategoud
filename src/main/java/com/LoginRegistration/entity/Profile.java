package com.LoginRegistration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
	
	private String token;
	private String username;
	private String firstname;
	private String lastname;
	private String dob;
	private String gender;
	private String phonenum;
	private String address;
}