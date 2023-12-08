package cl.bci.evaluacion.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CommonUtils {

	private CommonUtils() {
		// Private constructor to prevent instantiation
	}

	public static Date getCurrentDate() {

		// Get the current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Convert LocalDateTime to Date
		return Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static  String encodePassword(String password) {
		// Use a password encoder to securely encode the password
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}

}
