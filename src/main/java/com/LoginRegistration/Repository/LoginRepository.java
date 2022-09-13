package com.LoginRegistration.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.LoginRegistration.entity.Login;

@Repository
public interface LoginRepository extends JpaRepository<Login, Integer> {
	public Optional<Login> findByUsername(String username); 
	
	@Query(value = "select count(*) from login where username=:username", nativeQuery = true)
	public Integer findUsername(String username);

	public Optional<Login>findByUsernameAndPassword(String username, String password);

	@Modifying
	@Query(value = "UPDATE login set password=:password where username=:username", nativeQuery = true)
	public int updatePassword(@Param("username") String username, @Param("password") String password);

	@Modifying
	@Query(value = "UPDATE login set lastlogin=:lastlogin where username=:username", nativeQuery = true)
	public int updateLastLogin(@Param("username") String username, @Param("lastlogin") String lastlogin);

	
}