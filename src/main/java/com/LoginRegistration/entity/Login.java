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
@Table(name = "login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login {

	public Login(int userid, String username) {
		super();
		this.userid = userid;
		this.username = username;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userid;
	private String username;
	private String password;
	private String lastlogin;

	public Login(int userid) {
		super();
		this.userid = userid;
	}

}
