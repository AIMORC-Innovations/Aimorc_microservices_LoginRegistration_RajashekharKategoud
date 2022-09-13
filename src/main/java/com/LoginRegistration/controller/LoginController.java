package com.LoginRegistration.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import com.LoginRegistration.Exception.UserNotFoundException;
import com.LoginRegistration.Repository.LoginRepository;
import com.LoginRegistration.Repository.UserAddressRepository;
import com.LoginRegistration.entity.JwtRequest;
import com.LoginRegistration.entity.JwtResponse;
import com.LoginRegistration.entity.Login;
import com.LoginRegistration.entity.Password;
import com.LoginRegistration.entity.Profile;
import com.LoginRegistration.entity.UserAddress;
import com.LoginRegistration.entity.Userdata;
import com.LoginRegistration.helper.JWTUtil;
import com.LoginRegistration.services.LoginServices;
import com.google.gson.Gson;

@Controller
@CrossOrigin("*")
public class LoginController {
	@Autowired
	private LoginRepository loginRepo;

	@Autowired
	private LoginServices loginservices;

	@Autowired
	private JWTUtil jwtutil;
	
	@Autowired
	private LoginRepository loginRepository;
	
	@Autowired
	private UserAddressRepository useraddressrepository;
	

	@Autowired
	private AuthenticationManager authenicationManager;
	
	RestTemplate restTemplate = new RestTemplate();

	String sessionusername;

	@PostMapping("/getUserId")
	@ResponseBody
	public int getUserId(@RequestBody String username) {
		//System.out.println("User ID "+this.loginservices.getUserId(username));
		return this.loginservices.getUserId(username);

	}

	@RequestMapping(value = "/userLogin", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public ResponseEntity<?> generateToken(@RequestBody Login login, HttpServletRequest response) throws Exception {
		System.out.println(login);
		this.authenicationManager
				.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
		UserDetails userdetails = this.loginservices.loadUserByUsername(login.getUsername());
		String token = this.jwtutil.generateToken(userdetails);
		String username = (String) decodeToken(token);
		loginRepo.updateLastLogin(username, login.getLastlogin());
		return ResponseEntity.ok(new JwtResponse(token));
	}

	@PostMapping("/newUserRegistration")
	public String registeruser(@RequestBody Userdata newuser) throws UserNotFoundException {
		loginservices.registeruser(newuser);
		System.out.println("in newUserRegistration");
		return "redirect:/login";

	}

	@PostMapping("/forgotPassword")
	public void getIdAndAns(@RequestBody Password fp, HttpServletRequest request, HttpServletResponse response)
			throws UserNotFoundException, ServletException, IOException {

		if (loginservices.getIdAndAns(fp.getUsername(), fp.getSecurity_id(), fp.getSecurity_answer())) {
			response.setStatus(200);
		} else {
			response.sendError(401, "Invalid Authenication");

		}

	}

	@PostMapping("/setPassword")
	public ResponseEntity<String> setpassword(@RequestBody Password password, HttpServletRequest request) {
		System.out.println(password);
		String username = password.getUsername();
		String Password = password.getNewpassword();

		return this.loginservices.setpassword(password);

	}

	@PostMapping("/getSecurityIdAndAns")
	public void getSecurityIdAndAns(@RequestBody Password fp, HttpServletRequest request, HttpServletResponse response)
			throws UserNotFoundException, IOException {
		Object tokenUsername = decodeToken(fp.getToken());
		fp.setUsername((String) tokenUsername);
		if (loginservices.getSecurityIdAndAns(fp)) {
			response.setStatus(200);
		} else {
			response.sendError(401, "Invalid Authenication");

		}
	}

	@PostMapping("/updatePassword")
	public ResponseEntity<String> updatePassword(@RequestBody Password password, HttpServletRequest request) {
		Object tokenUsername = decodeToken(password.getToken());
		if (password.getToken() != null) {
			password.setUsername((String) tokenUsername);
			return this.loginservices.updatePassword(password);
		}
		return new ResponseEntity<String>("Password entered already exist!", HttpStatus.OK);
	}

	@PostMapping("/decodeToken")
	@ResponseBody
	public Object decodeToken(@RequestBody String token) {
		Base64.Decoder decoder = Base64.getDecoder();
		String[] chunks = token.split("\\.");
		String payload = new String(decoder.decode(chunks[1]));
		Map<String, Object> map = new Gson().fromJson(payload, Map.class);
		Object output = null;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getKey().equals("sub")) {
				output = entry.getValue();
			}

		}
		return output;
	}

	@PostMapping("/profile")
	@ResponseBody
	public Profile getProfileDetails(@RequestBody JwtResponse jwtresponse, HttpServletResponse response)
			throws UserNotFoundException, IOException {
		Object tokenUsername = decodeToken(jwtresponse.getToken());
		Profile profiledetails = loginservices.getProfileDetails((String) tokenUsername);
		return profiledetails;
	}
	
	@PostMapping("/userAddressProfile")
	@ResponseBody
	public Map<String, Object> getUserAddressDetails(@RequestBody JwtResponse jwtresponse, HttpServletResponse response)
			throws UserNotFoundException, IOException {
		Object tokenUsername = decodeToken(jwtresponse.getToken());
		Map<String, Object> useraddressdetails = loginservices.getUserAddressDetails((String) tokenUsername);

		System.out.println("controller" + useraddressdetails);
		return useraddressdetails; //useraddressdetails
	}
	

	@PostMapping("/updateProfileDetails")
	@ResponseBody
	public ResponseEntity<String> updateuserdetails(@RequestBody Profile profile, HttpServletRequest request)
			throws UserNotFoundException {
		Object tokenUsername = decodeToken(profile.getToken());
		profile.setUsername((String) tokenUsername);
		return this.loginservices.updateuserdetails(profile);
	}
	
	
	@PostMapping("/updateUserAddressDetails")
	@ResponseBody
	public ResponseEntity<String> updateuseraddressdetails(@RequestBody Profile profile, HttpServletRequest request)
			throws UserNotFoundException {
		Object tokenUsername = decodeToken(profile.getToken());
		profile.setUsername((String) tokenUsername);
		return this.loginservices.updateuseraddressdetails(profile);
	}
	@PostMapping("/deleteAddress")
	@ResponseBody
	public ResponseEntity<String> deleteAddress(@RequestBody UserAddress userAddress, HttpServletRequest request)throws UserNotFoundException{
		String token = userAddress.getToken();
		String tokenUsername = (String) decodeToken(token);
		int result = getUserId(tokenUsername);
		int userid= result;
		System.out.println("userid  "+userid);
		System.out.println(userAddress);
		return this.loginservices.deleteAddress(userAddress, userid);
	}
	
	@PostMapping("/editAddress")
	@ResponseBody
	public ResponseEntity<String> editAddress(@RequestBody UserAddress userAddress, HttpServletRequest request) throws UserNotFoundException{ //Userdata userData
		String token = userAddress.getToken();
		String tokenUsername = (String) decodeToken(token);
		int result = getUserId(tokenUsername);
		int userid= result;
		System.out.println("userid  "+userid);
		System.out.println(userAddress);
        return this.loginservices.editAddress(userAddress, userid);
	}  
	
	
	@RequestMapping(value = "/addAddress", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public void schedulingPickUpAddr(@RequestBody Profile userData) throws UserNotFoundException{ //Userdata userData
		System.out.println("1");   
		String token = userData.getToken();
		String tokenUsername = (String) decodeToken(token);
		int result = getUserId(tokenUsername);
		int userid= result;
		
		Profile profile = new Profile();
		Integer numberOfAddress = useraddressrepository.fetchNumberOfAddress(userid);	
		System.out.println(numberOfAddress);
		System.out.println("4");
		if(numberOfAddress<5) {
			loginservices.adduseraddressdetails(userData, userid);
		}else {
			System.out.println("User can't save more than 5 address");
		}
	}  
	
	
}
