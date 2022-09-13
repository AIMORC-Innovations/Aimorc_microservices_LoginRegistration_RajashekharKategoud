package com.LoginRegistration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Userdata {
	private String username;
	private String password;
	private String lastlogin;
	private String firstname;
	private String lastname;
	private String dob;
	private String gender;
	private String address;
	private String address1;
	private String city;
	private String state;
	private String country;
	private String zip;
	private String phonenum;
	private String created_on;
	private int security_id;
	private String security_answer;
}
