package cl.bci.evaluacion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cl.bci.evaluacion.dto.SignUpRequestDTO;
import cl.bci.evaluacion.entity.UserEntity;
import cl.bci.evaluacion.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

      
    
    @Test
    public void testCreateUser() {
    	
        // Mocking the UserRepository 
        UserEntity savedUser = new UserEntity(); // Create a sample saved user
        
        // Solo los datos básicos
        savedUser.setActive(true);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword(this.encodePassword("Testbci23"));
                
        
        when(userRepository.save(any())).thenReturn(savedUser);

        // Creating a UserRequestDTO for testing
        SignUpRequestDTO userRequestDTO = new SignUpRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john@example.com");
        userRequestDTO.setPassword("Testbci23");        

        // Calling the createUser method
        userService.createUser(userRequestDTO);

        // Verifying that the save method was called with the expected User entity
        verify(userRepository, times(1)).save(any());
        
        logger.info("post test email {}", savedUser.getEmail());
        logger.info("post test name {}", savedUser.getName());
        logger.info("post test password {}", savedUser.getPassword());

        assertNotNull(savedUser);
        
        // Se setearon los valores correctos
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());

    }
    
    
    
    @Test
    public void getUserByToken() {
    	
        // Mocking the UserRepository
    	String existingEmail = "jose.cavieres2@gmail.com";
    	
    	// Usuario existente
        UserEntity existingUser = new UserEntity(); // Create a sample saved user

        existingUser.setActive(true);
        existingUser.setName("John Doe");
        existingUser.setEmail(existingEmail);
        existingUser.setPassword(this.encodePassword("Testbci23"));
                
        // responder con el mock        
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(existingUser);        
        
        // responde el mock con el mismo usuario
        when(userRepository.save(any())).thenReturn(existingUser);
        
        
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb3NlLmNhdmllcmVzMkBnbWFpbC5jb20iLCJpYXQiOjE3MDEyOTIyODgsImV4cCI6MTcwMTM3ODY4OH0.5IB6aep4moVQZodHOYlFI75_LNWjP1FmXTovK-7o3M";

        // Calling the createUser method
        userService.getUserByToken(token);
        
        // Verify that the findByEmail method was called at least once
        verify(userRepository, times(1)).findByEmail(existingEmail);

        // Verificar que se llamo al menos una vez el metodo save
        verify(userRepository, times(1)).save(any());

        assertNotNull(existingUser);
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