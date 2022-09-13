package com.LoginRegistration.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Register {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int regid;
    private int userid;
	private String firstname;
	private String lastname;
	private String dob;
	private String gender;
	private String phonenum;
	private String address;
	private String address1;
	private String city;
	private String state;
	private String country;
	private String zip;
	private int security_id;
	private String security_answer;
	private String created_on;
}