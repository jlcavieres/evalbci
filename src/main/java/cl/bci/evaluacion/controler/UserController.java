package cl.bci.evaluacion.controler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.bci.evaluacion.dto.ErrorResponseDTO;
import cl.bci.evaluacion.dto.ErroresResponseDTO;
import cl.bci.evaluacion.dto.LoginResponseDTO;
import cl.bci.evaluacion.dto.PhoneDTO;
import cl.bci.evaluacion.dto.SignUpRequestDTO;
import cl.bci.evaluacion.dto.SignUpResponseDTO;
import cl.bci.evaluacion.entity.PhoneEntity;
import cl.bci.evaluacion.entity.UserEntity;
import cl.bci.evaluacion.exception.InvalidTokenException;
import cl.bci.evaluacion.exception.UserAlreadyExistsException;
import cl.bci.evaluacion.exception.UserNotFoundException;
import cl.bci.evaluacion.util.CommonUtils;
import cl.evaluacion.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDTO userDTO, BindingResult result) {

		// Manejo de errores de sintaxis y formato del input (uso de Spring Validation)
		if (result.hasErrors()) {
			logger.info("-- Encontró un error en sign-up (hasError del binding)");
			return new ResponseEntity<>(this.convertErrorToDTO(result.getFieldErrors()), HttpStatus.BAD_REQUEST);
		}

		ErroresResponseDTO errores = new ErroresResponseDTO();
		List<ErrorResponseDTO> formatedErrors = new ArrayList<ErrorResponseDTO>();

		try {

			UserEntity createdUser = userService.createUser(userDTO);
			return new ResponseEntity<>(this.mapSignUpResponse(createdUser), HttpStatus.CREATED);

		} catch (UserAlreadyExistsException e) {

			// Manejo de errores de lógica lanzados desde el service

			logger.error("Cayo en el catch de SignUp UserAlreadyExistsException");

			HttpStatus hs = HttpStatus.CONFLICT;
			formatedErrors
					.add(new ErrorResponseDTO(new Timestamp(System.currentTimeMillis()), hs.value(), e.getMessage()));
			errores.setErrores(formatedErrors);

			return new ResponseEntity<>(errores, hs);

		} catch (Exception e) {

			// Manejo de posible excepción no controlada
			HttpStatus hs = HttpStatus.INTERNAL_SERVER_ERROR;
			formatedErrors.add(new ErrorResponseDTO(new Timestamp(System.currentTimeMillis()), hs.value(),
					"Internal server error"));
			errores.setErrores(formatedErrors);

			return new ResponseEntity<>(errores, hs);
		}
	}

	@GetMapping("/login")
	public ResponseEntity<?> login(HttpServletRequest request) {

		UserEntity response = null;

		// Extract the authorization token from the request header
		String authorizationHeader = request.getHeader("Authorization");

		logger.info("AuthorizationHeader = {}", authorizationHeader);

		ErroresResponseDTO errores = new ErroresResponseDTO();
		List<ErrorResponseDTO> formatedErrors = new ArrayList<ErrorResponseDTO>();

		try {

			// Check if the Authorization header is present and starts with "Bearer "
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				throw new InvalidTokenException();
			}

			// Extract the token (remove "Bearer " prefix)
			String token = authorizationHeader.substring(7);
			logger.debug("Token enviado = {} ", token);

			response = userService.getUserByToken(token);

			return new ResponseEntity<>(this.mapLoginResponse(response), HttpStatus.OK);

		} catch (UserNotFoundException e) {

			logger.info("Catch de Login UserNotFoundException");

			HttpStatus hs = HttpStatus.NOT_FOUND;
			formatedErrors
					.add(new ErrorResponseDTO(new Timestamp(System.currentTimeMillis()), hs.value(), e.getMessage()));
			errores.setErrores(formatedErrors);

			return new ResponseEntity<>(errores, hs);
		} catch (InvalidTokenException e) {

			logger.info("Catch de Login InvalidTokenException");

			HttpStatus hs = HttpStatus.NOT_FOUND;
			formatedErrors
					.add(new ErrorResponseDTO(new Timestamp(System.currentTimeMillis()), hs.value(), e.getMessage()));
			errores.setErrores(formatedErrors);

			return new ResponseEntity<>(errores, hs);

		} catch (Exception e) {

			logger.error("Cayó en el catch de Login excepción no controlada");

			// Manejo de posible excepción no controlada
			HttpStatus hs = HttpStatus.INTERNAL_SERVER_ERROR;
			formatedErrors.add(new ErrorResponseDTO(new Timestamp(System.currentTimeMillis()), hs.value(),
					"Internal server error"));
			errores.setErrores(formatedErrors);

			return new ResponseEntity<>(errores, hs);
		}
	}

	private ErroresResponseDTO convertErrorToDTO(List<FieldError> springErrores) {

		ErroresResponseDTO errores = new ErroresResponseDTO();
		List<ErrorResponseDTO> formatedErrors = new ArrayList<ErrorResponseDTO>();

		for (FieldError springError : springErrores) {

			// Get the current timestamp in milliseconds since the epoch
			long currentTimeMillis = System.currentTimeMillis();

			// Convert the current timestamp to a Timestamp object
			Timestamp timestamp = new Timestamp(currentTimeMillis);

			ErrorResponseDTO fError = new ErrorResponseDTO();

			fError.setCodigo(HttpStatus.BAD_REQUEST.value());
			fError.setDetail(springError.getDefaultMessage());
			fError.setTimestamp(timestamp);

			formatedErrors.add(fError);
		}

		errores.setErrores(formatedErrors);

		return errores;
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, UserAlreadyExistsException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {

		logger.error("-- Cayó en el exception handler para validaciones");

		BindingResult result = ex.getBindingResult();

		Map<String, String> errors = new HashMap<>();
		result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	private SignUpResponseDTO mapSignUpResponse(UserEntity entity) {

		SignUpResponseDTO response = new SignUpResponseDTO();

		response.setId(entity.getId());
		response.setCreated(entity.getCreated());
		response.setToken(entity.getToken());
		response.setActive(entity.isActive());
		response.setLastLogin(entity.getLastLogin());

		return response;
	}
	
	
	private LoginResponseDTO mapLoginResponse(UserEntity entity) {

		LoginResponseDTO response = new LoginResponseDTO();

		response.setId(entity.getId().toString());
		response.setCreated(entity.getCreated());
		response.setToken(entity.getToken());
		response.setActive(entity.isActive());
		response.setName(entity.getName());
		response.setEmail(entity.getName());
		response.setPassword(entity.getPassword());
		response.setLastLogin(entity.getLastLogin());
		response.setPhones(this.mapPhoneResponse(entity.getPhones()));

		return response;
	}
	
	
	private List<PhoneDTO> mapPhoneResponse(List<PhoneEntity> sourceDTO) {
		
		List<PhoneDTO> destinationList = new ArrayList<>();

		if (sourceDTO != null && !sourceDTO.isEmpty()) {			

			// El uso de lambda expression está disponible a partir de la versión 8 de Java
			sourceDTO.forEach(phone -> destinationList.add(new PhoneDTO(phone.getNumber(), phone.getCitycode(), phone.getCountrycode())));
		}
		
		return destinationList;		
	}
	
	
	
	private UserEntity convertRequestToUserEntity(SignUpRequestDTO sourceDTO) {
		
		// Convert DTO to Entity and save to database
		UserEntity userEntity = new UserEntity();

		userEntity.setName(sourceDTO.getName());
		userEntity.setEmail(sourceDTO.getEmail());
		userEntity.setPassword(CommonUtils.encodePassword(sourceDTO.getPassword()));

		if (sourceDTO.getPhones() != null && !sourceDTO.getPhones().isEmpty()) {

			List<PhoneEntity> destinationList = new ArrayList<>();

			// El uso de lambda expression está disponible a partir de la versión 8 de Java
			sourceDTO.getPhones().forEach(phone -> destinationList
					.add(new PhoneEntity(phone.getNumber(), phone.getCitycode(), phone.getCountrycode())));

			userEntity.setPhones(destinationList);
		}
		
		return userEntity;		
	}

}
