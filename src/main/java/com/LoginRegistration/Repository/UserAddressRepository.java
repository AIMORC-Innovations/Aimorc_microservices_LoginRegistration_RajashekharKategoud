package com.LoginRegistration.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.LoginRegistration.entity.UserAddress;

@Transactional
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer>{

	@Query(value = "select * from user_address where  userid=?", nativeQuery = true)
	public List<Map<String, Object>> findById(@Param("userid") int userid); //UserAddress
	
	@Query(value = "select address_id, country, state, city, zip, address, address1, userid from user_address where  userid=?", nativeQuery = true)
	public List<Map<String, Object>> findByUserId(@Param("address_id")  int address_id, @Param("userid") int userid );
	
//	@Query(value = "select * from user_address where  userid=?", nativeQuery = true)
//	public Integer findByUserId(@Param("address_id")  int address_id, @Param("userid") int userid);
//	
	
	@Query(value ="select count(*) from user_address where userid=?",nativeQuery = true) //select count(*) from date where userid=:?
	public Integer fetchNumberOfAddress(@Param("userid")int userid);
	
	@Query(value = "insert into user_address(country,state,city,address,address1,zip,userid) values (:userid,:country,:state,:city,:address,:address1,::zip,)",nativeQuery = true)
	public UserAddress save(@Param("userid")int userid);
	
	
	
	@Query(value = "insert into user_address(country,state,city,address,address1,zip,userid) values (:userid,:country,:state,:city,:address1,:address,:zip,)",nativeQuery = true)
    public UserAddress saveAddress(@Param("address") String address, @Param("address1") String address1,@Param("country") String country,@Param("city") String city,@Param("state") String state, @Param("zip")String zip, @Param("userid")int userid);
	
	@Modifying
	@Query(value = "delete from user_address where address_id=:address_id and userid=:userid ",nativeQuery = true)
    public void deleteAddressDetails(@Param("address_id") int address_id, @Param("userid")int userid); //Param("address_id") int address_id,
	
	
	@Modifying
	@Query(value = "UPDATE user_address set address=:address, address1=:address1, city=:city, state=:state, country=:country, zip=:zip where userid=:userid", nativeQuery = true)
	public void updateDetails(@Param("address") String address, @Param("address1") String address1, @Param("city") String city, 
			@Param("state") String state, @Param("country") String country, @Param("zip") String zip,
			@Param("userid") int userid);
	
	@Modifying
	@Query(value = "UPDATE user_address set address=:address, address1=:address1, city=:city, state=:state, country=:country, zip=:zip where address_id=:address_id and userid=:userid", nativeQuery = true)
	public void updateAddressDetails(@Param("address") String address, @Param("address1") String address1, @Param("city") String city, 
			@Param("state") String state, @Param("country") String country, @Param("zip") String zip, @Param("address_id") int address_id, //@Param("address_id") int address_id,
			@Param("userid") int userid);
}
