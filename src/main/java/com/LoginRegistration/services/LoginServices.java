package com.LoginRegistration.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
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
import com.LoginRegistration.entity.Password;
import com.LoginRegistration.entity.Constants;
import com.LoginRegistration.entity.Login;
import com.LoginRegistration.entity.Profile;
import com.LoginRegistration.entity.Register;
import com.LoginRegistration.entity.Userdata;

@Service
public class LoginServices implements UserDetailsService {
	@Autowired
	private Constants constants;


	@Autowired
	private LoginRepository loginRepository;

	@Autowired
	private RegisterRepository registerRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Login> logins = this.loginRepository.findByUsername(username);
		if (logins.get().getUsername().equals(username)) {
			String str = logins.get().getPassword();
			Base64.Decoder decoder = Base64.getDecoder();
			String dStr = new String(decoder.decode(str)); 
			
			 return new User(logins.get().getUsername(), dStr, new ArrayList<>());
		} else {
			return (UserDetails) new ResponseEntity<String>("Invalid Credentials", HttpStatus.FORBIDDEN);
		}
	}
	 
	
	public int getUserId(String username) {
		int userid = loginRepository.findByUsername(username).get().getUserid();
		return userid;
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
		return profile;
	}

	@Transactional
	public ResponseEntity<String> updateuserdetails(Profile profile) {
		int userid = loginRepository.findByUsername(profile.getUsername()).get().getUserid();
		registerRepository.updateDetails(profile.getFirstname(), profile.getLastname(), profile.getDob(),
				profile.getGender(), profile.getPhonenum(), profile.getAddress(), userid);
		return new ResponseEntity<String>("Details updated succesfully", HttpStatus.OK);

	}

	public boolean registeruser(Userdata newuser) {
		Login login = new Login();
		Register register = new Register();
		
		String encryptedpassword = getEncodedString(newuser.getPassword());
		Optional<Login> existingusername = loginRepository.findByUsername(newuser.getUsername());
		login.setUsername(newuser.getUsername());
		login.setPassword(encryptedpassword);
		login.setLastlogin(newuser.getCreated_on());
		if (login.getUsername().equals(existingusername)) {
			new ResponseEntity<String>("User already exists Please Login!", HttpStatus.ACCEPTED);
			return false;
		}
		loginRepository.save(login);
		int userid = getUserId(login.getUsername());
		register.setUserid(userid);
		register.setFirstname(newuser.getFirstname());
		register.setLastname(newuser.getLastname());
		register.setDob(newuser.getDob());
		register.setGender(newuser.getGender());
		register.setPhonenum(newuser.getPhonenum());
		register.setAddress(newuser.getAddress());
		register.setCreated_on(newuser.getCreated_on());
		register.setSecurity_id(newuser.getSecurity_id());
		register.setSecurity_answer(newuser.getSecurity_answer());
		registerRepository.save(register);
		sendEmail(constants.message, constants.subject, login.getUsername(),constants. from);
		new ResponseEntity<String>("success!", HttpStatus.ACCEPTED);
		return true;
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

	

}