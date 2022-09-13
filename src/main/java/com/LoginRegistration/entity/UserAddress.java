package com.LoginRegistration.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int address_id; //int
	private String token;
    private int userid;
	private String address;
	private String address1;
	private String city;
	private String state;
	private String country;
	private String zip;
	

}
