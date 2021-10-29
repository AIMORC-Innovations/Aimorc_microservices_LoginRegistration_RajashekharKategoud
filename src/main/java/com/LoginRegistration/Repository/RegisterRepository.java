package com.LoginRegistration.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.LoginRegistration.entity.Register;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Integer> {

	@Query(value = "select * from registration where  userid=?", nativeQuery = true)
public Register findById(@Param("userid") int userid);


	@Modifying
	@Query(value = "UPDATE registration set firstname=:firstname,lastname=:lastname,dob=:dob,gender=:gender,phonenum=:phonenum, address=:address where userid=:userid", nativeQuery = true)
	public void updateDetails(@Param("firstname") String firstname, @Param("lastname") String lastname,
			@Param("dob") String dob, @Param("gender") String gender, @Param("phonenum") String phonenum,
			@Param("address") String address, @Param("userid") int userid);

}