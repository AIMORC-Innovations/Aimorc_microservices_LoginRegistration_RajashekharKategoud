package com.LoginRegistration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Password {
	private String token;
	private String username;
	private int security_id;
	private String security_answer;
	private String oldpassword;
	private String newpassword;
	
}