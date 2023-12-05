package com.LoginRegistration.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.LoginRegistration.Exception.UserNotFoundException;
import com.LoginRegistration.Repository.LoginRepository;
import com.LoginRegistration.Repository.RegisterRepository;
import com.LoginRegistration.Repository.UserAddressRepository;
import com.LoginRegistration.entity.Password;
import com.LoginRegistration.entity.Constants;
import com.LoginRegistration.entity.Login;
import com.LoginRegistration.entity.Profile;
import com.LoginRegistration.entity.Register;
import com.LoginRegistration.entity.Userdata;
import com.LoginRegistration.entity.UserAddress;

@Service
public class LoginServices implements UserDetailsService {
	@Autowired
	private Constants constants;


	@Autowired
	private LoginRepository loginRepository;

	@Autowired
	private RegisterRepository registerRepository;
	
	@Autowired
	private UserAddressRepository useraddressrepository;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Login> logins = this.loginRepository.findByUsername(username);
		System.out.println(logins+"logins");
		if (logins.get().getUsername().equals(username)) {
			String str = logins.get().getPassword();
			String role = logins.get().getRole();
			System.out.println(role+"-role-");
			Base64.Decoder decoder = Base64.getDecoder();
			String dStr = new String(decoder.decode(str)); 
			
			 return new User(logins.get().getUsername(), dStr, new ArrayList<>());
		} else {
			return (UserDetails) new ResponseEntity<String>("Invalid Credentials", HttpStatus.FORBIDDEN);
		}
	}
	 
	
	public int getUserId(String username)  {
		int userid = loginRepository.findByUsername(username).get().getUserid();
		return userid;
	}
	

	public Login getRole(String username) throws UsernameNotFoundException{
		Login logins = this.loginRepository.findRole(username);
		System.out.println(logins+"---");
		//String role = logins.get().getRole();
		//System.out.println("role   "+role);
		return logins;
		//return new User(logins.get().getUsername(), logins.get().getRole(), new ArrayList<>());
	}
	

	public static String getEncodedString(String password) {
		String encrytedpassword = Base64.getEncoder().encodeToString(password.getBytes());
		return encrytedpassword;
	}
	
	
	@Transactional
	public boolean getUserDetails(String username,
			String password/* , String lastlogin */)
			throws UserNotFoundException {
		String encryptedpassword = getEncodedString(password);
		Login login = new Login();
		Optional<Login> logins = this.loginRepository.findByUsername(username);
		loginRepository.findByUsernameAndPassword(username, encryptedpassword);
		if (logins.get().getUsername().equals(username)
				&& logins.get().getPassword().toString().equalsIgnoreCase(encryptedpassword)) {
		
			return true;
		} else {
			
			return false;
		}
	}

	public boolean getIdAndAns(String username, int security_id, String security_answer) throws UserNotFoundException {

		int userid = loginRepository.findByUsername(username).get().getUserid();
	
		Register register = registerRepository.findById(userid);
		System.out.println(register);
		Register security = new Register();
		security.setSecurity_id(register.getSecurity_id());
		security.setSecurity_answer(register.getSecurity_answer());
		if (register.getSecurity_id() == security_id
				&& register.getSecurity_answer().equals(security_answer)) {
		
            return true;
		}
		
		return false;
	}

	@Transactional
	public ResponseEntity<String> setpassword(Password passowrd) {
		Optional<Login> logins = this.loginRepository.findByUsername(passowrd.getUsername());
		String encryptedpassword = getEncodedString(passowrd.getNewpassword());
		
			loginRepository.updatePassword(passowrd.getUsername(), encryptedpassword);
			return new ResponseEntity<String>("Password updated succesfully", HttpStatus.OK);
		


	}

	public boolean getSecurityIdAndAns(Password fp) {
		int userid = loginRepository.findByUsername(fp.getUsername()).get().getUserid();
		
		Register register = registerRepository.findById(userid);
		Register security = new Register();
		security.setSecurity_id(register.getSecurity_id());
		security.setSecurity_answer(register.getSecurity_answer());
		if (register.getSecurity_id() == fp.getSecurity_id()
				&& register.getSecurity_answer().toString().equalsIgnoreCase(fp.getSecurity_answer()))
		
			return true;
		
		return false;
	}

	@Transactional
	public ResponseEntity<String> updatePassword(Password password) {
		Optional<Login> userid = this.loginRepository.findByUsername(password.getUsername());
		String encryptedoldpassword = getEncodedString(password.getOldpassword());
		String encryptednewpassword = getEncodedString(password.getNewpassword());
		if (userid.get().getPassword().toString().equals(encryptedoldpassword)) {
			loginRepository.updatePassword(password.getUsername(), encryptednewpassword);
			return new ResponseEntity<String>("Password updated succesfully", HttpStatus.OK);
		}
		if (encryptedoldpassword.toString().equals(encryptednewpassword)) {
			return new ResponseEntity<String>("Old password is same as new password! give some different password",
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<String>("Check Credentials and update!", HttpStatus.UNAUTHORIZED);

	}
	
	@Transactional
	public ResponseEntity<String> changePassword(Password password) {
		Optional<Login> userid = this.loginRepository.findByUsername(password.getUsername());
		System.out.println(userid+"thius is changepassword");
		System.out.println(password.getNewpassword());
	
		String encryptednewpassword = getEncodedString(password.getNewpassword());
		
			loginRepository.updatePassword(password.getUsername(), encryptednewpassword);
			return new ResponseEntity<String>("Password updated succesfully", HttpStatus.OK);
		


	}

	public Profile getProfileDetails(String sessionusername) throws UserNotFoundException {
		int userid = loginRepository.findByUsername(sessionusername).get().getUserid();
		Register register = registerRepository.findById(userid);
		Profile profile = new Profile();
		profile.setFirstname(register.getFirstname());
		profile.setLastname(register.getLastname());
		profile.setDob(register.getDob());
		profile.setPhonenum(register.getPhonenum());
		profile.setGender(register.getGender());
		profile.setAddress(register.getAddress());
		profile.setAddress1(register.getAddress1());
		profile.setCity(register.getCity());
		profile.setState(register.getState());
		profile.setCountry(register.getCountry());
		profile.setZip(register.getZip());
		return profile;
	}
	
	public Map<String, Object> getUserAddressDetails(String sessionusername) throws UserNotFoundException {
		int userid = loginRepository.findByUsername(sessionusername).get().getUserid();
		Map<String, Object> allAddress = new HashMap<String, Object>(); //List<Map<String, Object>> allAddress = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> alladdresslist = useraddressrepository.findById(userid);
			for (Map<String, Object> eachaddress : alladdresslist) {
				int addressId = (int) eachaddress.get("address_id");
				String address = (String) eachaddress.get("address");
				String address1 = (String) eachaddress.get("address1");
				String city = (String) eachaddress.get("city");
				String state = (String) eachaddress.get("state");
				String country = (String) eachaddress.get("country");
				String zip =  (String) eachaddress.get("zip");
				
//				List<Map<String, Object>> allAddressBasedOnUserId = useraddressrepository.findByUserId(addressId, userid);
				Map<String, Object> eachAddressMap = new HashMap<String, Object>();
				eachAddressMap.put("addressId", addressId);
				eachAddressMap.put("address", address); //allAddressBasedOnUserId //alladdresslist
				eachAddressMap.put("address1", address1);
				eachAddressMap.put("city", city);
				eachAddressMap.put("state", state);
				eachAddressMap.put("country", country);
				eachAddressMap.put("zip", zip);
				//eachAddressMap.put("alladdresslist", alladdresslist);
				String aid = String.valueOf(addressId);
				allAddress.put(aid, eachAddressMap); //eachAddressMap
				System.out.println("alladdresslist" + alladdresslist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("allAddress" + allAddress);
		return allAddress; //allAddress
	}
	

	@Transactional
	public ResponseEntity<String> updateuserdetails(Profile profile) {
		int userid = loginRepository.findByUsername(profile.getUsername()).get().getUserid();
		registerRepository.updateDetails(profile.getFirstname(), profile.getLastname(), profile.getDob(),
				profile.getGender(), profile.getPhonenum(),  userid); //profile.getAddress(), profile.getAddress1(), profile.getCity(), profile.getState(), profile.getCountry(), profile.getZip(),  
		return new ResponseEntity<String>("Details updated succesfully", HttpStatus.OK);

	}
	
	public ResponseEntity<String> updateuseraddressdetails(Profile profile ) {
		int userid = loginRepository.findByUsername(profile.getUsername()).get().getUserid();
		useraddressrepository.updateDetails(profile.getAddress(), profile.getAddress1(),
				profile.getCity(), profile.getState(), profile.getCountry(), profile.getZip(), userid);
		return new ResponseEntity<String>("Details updated succesfully", HttpStatus.OK);
	}
	
	public UserAddress adduseraddressdetails(Profile userData, int userid) {
		// TODO Auto-generated method stub
		UserAddress useraddress=new UserAddress();
		useraddress.setUserid(userid);
		useraddress.setAddress(userData.getAddress());
		useraddress.setAddress1(userData.getAddress1());
		useraddress.setCity(userData.getCity());
		useraddress.setState(userData.getState());
		useraddress.setCountry(userData.getCountry());
		useraddress.setZip(userData.getZip());
		return useraddressrepository.save(useraddress);
	}
	
	public ResponseEntity<String> deleteAddress(UserAddress userAddress, int userid) {
		System.out.println("addressId " + userAddress.getAddress_id());
		useraddressrepository.deleteAddressDetails(userAddress.getAddress_id(), userid);
		return new ResponseEntity<String>("Details updated succesfully", HttpStatus.OK);
	}
	
	public ResponseEntity<String> editAddress(UserAddress userAddress, int userid) {
		useraddressrepository.updateAddressDetails(userAddress.getAddress(), userAddress.getAddress1(),
				userAddress.getCity(), userAddress.getState(), userAddress.getCountry(), userAddress.getZip(), userAddress.getAddress_id(), userid);
		return new ResponseEntity<String>("Details updated succesfully", HttpStatus.OK);
	}
	

	public boolean registeruser(Userdata newuser) {
		Login login = new Login();
		Register register = new Register();
		UserAddress useraddress=new UserAddress();
		
		
		String encryptedpassword = getEncodedString(newuser.getPassword());
		Optional<Login> existingusername = loginRepository.findByUsername(newuser.getUsername()); 
		//String existingUsername = existingusername.map(Login::toString).orElse(null);
		Integer existingUsername = loginRepository.findUsername(newuser.getUsername());
		login.setUsername(newuser.getUsername());
		login.setPassword(encryptedpassword);
		login.setLastlogin(newuser.getCreated_on());
		login.setRole("user");
		
		System.out.println("existingusername " + existingusername);
		System.out.println("existingUsername " + existingUsername);
		if(existingUsername==1) { //login.getUsername().equals(existingUsername) or existingUsername==1
			new ResponseEntity<String>("User already exists Please Login!", HttpStatus.ACCEPTED);
			System.out.println("User already exists");
			return false;
		}else {
		loginRepository.save(login);

		int userid = getUserId(login.getUsername());
		useraddress.setUserid(userid);
		useraddress.setAddress(newuser.getAddress());
		useraddress.setAddress1(newuser.getAddress1());
		useraddress.setCity(newuser.getCity());
		useraddress.setState(newuser.getState());
		useraddress.setCountry(newuser.getCountry());
		useraddress.setZip(newuser.getZip());
		register.setUserid(userid);
		register.setFirstname(newuser.getFirstname());
		register.setLastname(newuser.getLastname());
		register.setDob(newuser.getDob());
		register.setGender(newuser.getGender());
		register.setPhonenum(newuser.getPhonenum());
		register.setAddress(newuser.getAddress());
		register.setAddress1(newuser.getAddress1());
		register.setCity(newuser.getCity());
		register.setState(newuser.getState());
		register.setCountry(newuser.getCountry());
		register.setZip(newuser.getZip());
		register.setCreated_on(newuser.getCreated_on());
		register.setSecurity_id(newuser.getSecurity_id());
		register.setSecurity_answer(newuser.getSecurity_answer());
		registerRepository.save(register);
		useraddressrepository.save(useraddress);
//		sendEmail(constants.message, constants.subject, login.getUsername(),constants. from);
		new ResponseEntity<String>("success!", HttpStatus.ACCEPTED);
		return true;
		}
	}

	private static void sendEmail(String message, String subject, String username, String from) {
		// variable for gmail
		String host = "smtp.gmail.com";
		// get system properties
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");// for security
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.trust", host);
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// for aunthenication //gettingSession object
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("aimorc.ecomm@gmail.com", "AIMORC@15");
			}

		});
		session.setDebug(true);
		// compose message
		MimeMessage mimemessage = new MimeMessage(session);
		try {
			// from email
			mimemessage.setFrom(from);
			// recepient email
			mimemessage.addRecipient(Message.RecipientType.TO, new InternetAddress(username));
			// adding subject to message
			mimemessage.setSubject(subject); // adding textmessage
			mimemessage.setText(message);
			// send
			Transport.send(mimemessage);
			} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public UserAddress updatePickUpAddr(String city, String country,String address, String address1, String state, String zip,int userid) {
		// TODO Auto-generated method stub
		return useraddressrepository.saveAddress(address,address1,country,city,state,zip,userid);
		}

	

}