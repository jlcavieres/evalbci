package cl.bci.evaluacion.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import cl.bci.evaluacion.dto.SignUpRequestDTO;
import cl.bci.evaluacion.entity.PhoneEntity;
import cl.bci.evaluacion.entity.UserEntity;
import cl.bci.evaluacion.exception.UserAlreadyExistsException;
import cl.bci.evaluacion.exception.UserNotFoundException;
import cl.bci.evaluacion.repository.UserRepository;
import cl.bci.evaluacion.security.JwtUtils;
import cl.evaluacion.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public UserEntity createUser(SignUpRequestDTO userDTO) {

		// Validación de lógica de negocio
		boolean exist = userRepository.existsByEmail(userDTO.getEmail());

		logger.info("El email existe pregunto por {} y respondio = {} ", userDTO.getEmail(), exist);

		// Check if the user already exists
		if (exist) {
			throw new UserAlreadyExistsException();
		}		
		
		// Crear token JWT
		String token = jwtUtils.generateToken(userDTO.getEmail());
		
		UserEntity userEntity = this.convertRequestToUserEntity(userDTO);
		
		// Los que estan fuera de la DTO del request
		userEntity.setCreated(this.getCurrentDate());
		// userEntity.setLastLogin(null); // remover si por defecto lo deja en null al crear
		userEntity.setActive(true);
		userEntity.setToken(token);

		return userRepository.save(userEntity);
	}
	
	
	
	private UserEntity convertRequestToUserEntity(SignUpRequestDTO sourceDTO) {
		
		// Convert DTO to Entity and save to database
		UserEntity userEntity = new UserEntity();

		userEntity.setName(sourceDTO.getName());
		userEntity.setEmail(sourceDTO.getEmail());
		userEntity.setPassword(this.encodePassword(sourceDTO.getPassword()));

		if (sourceDTO.getPhones() != null && !sourceDTO.getPhones().isEmpty()) {

			List<PhoneEntity> destinationList = new ArrayList<>();

			// El uso de lambda expression está disponible a partir de la versión 8 de Java
			sourceDTO.getPhones().forEach(phone -> destinationList
					.add(new PhoneEntity(phone.getNumber(), phone.getCitycode(), phone.getCountrycode())));

			userEntity.setPhones(destinationList);
		}
		
		return userEntity;		
	}
	 

	/**
	 * 
	 * @param token puro, sin prefijos
	 */
	@Override
	public UserEntity getUserByToken(String token) {


		// Con proposito demostrativo, no uso el token como llave, extraigo el sub (email) del
		// token yen base a eso busco el dato del usuario por email
		String email = jwtUtils.extractSubject(token);

		logger.debug("-- Email extraido del subject del token {} ", email);

		UserEntity userEntity = userRepository.findByEmail(email); 

		if (userEntity == null) { 
			throw new UserNotFoundException();
		}
		

		// Acorde a lo solicitado, se genera y asigna un nuevo token al usuario

		String newToken = jwtUtils.generateToken(email);
		userEntity.setToken(newToken);
		userEntity.setLastLogin(this.getCurrentDate());

		return userRepository.save(userEntity);
	}
	
	
	
	private Date getCurrentDate() {
		
		// Get the current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Convert LocalDateTime to Date
		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		
		return currentDate;		
	}

	private String encodePassword(String password) {
		// Use a password encoder to securely encode the password
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
	
}
