package cl.bci.evaluacion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cl.bci.evaluacion.dto.PhoneDTO;
import cl.bci.evaluacion.dto.SignUpRequestDTO;
import cl.bci.evaluacion.entity.UserEntity;
import cl.bci.evaluacion.exception.UserAlreadyExistsException;
import cl.bci.evaluacion.exception.UserNotFoundException;
import cl.bci.evaluacion.repository.UserRepository;
import cl.bci.evaluacion.security.JwtUtils;
import cl.bci.evaluacion.service.impl.UserServiceImpl;
import cl.bci.evaluacion.util.CommonUtils;
import cl.evaluacion.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    
    @Mock
    private UserRepository userRepositoryMock;
    
    
    @Mock
    private JwtUtils jwtUtilsMock;
    
    
    @InjectMocks
    private UserServiceImpl userService;

       
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
      
    
    @Test
    void testCreateUser() {
    	
        // Mocking the UserRepository 
        UserEntity savedUser = new UserEntity(); // Create a sample saved user
        
        // Solo los datos básicos
        savedUser.setActive(true);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword(CommonUtils.encodePassword("Testbci23"));
        
        when(userRepositoryMock.save(any())).thenReturn(savedUser);

        // Creating a UserRequestDTO for testing
        SignUpRequestDTO userRequestDTO = new SignUpRequestDTO();
        userRequestDTO.setName("John Doe");
        userRequestDTO.setEmail("john@example.com");
        userRequestDTO.setPassword("Testbci23");        

        // Calling the createUser method
        UserEntity usuarioCreado = userService.createUser(userRequestDTO);

        // Verifying that the save method was called with the expected User entity
        verify(userRepositoryMock, times(1)).save(any());
        
        
        assertNotNull(usuarioCreado);
        
        logger.info("post test email {}", usuarioCreado.getEmail());
        logger.info("post test name {}", usuarioCreado.getName());
        logger.info("post test password {}", usuarioCreado.getPassword());        
        
        // Se setearon los valores correctos
        assertEquals("John Doe", usuarioCreado.getName());
        assertEquals("john@example.com", usuarioCreado.getEmail());

    }
    

    @Test
    void testCreateUser_UserAlreadyExistsException() {
        // Arrange
    	SignUpRequestDTO userDTO = new SignUpRequestDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password");

        when(userRepositoryMock.existsByEmail(userDTO.getEmail())).thenReturn(true);

        // Se asegura que la excepción se ha lanzado
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDTO));

        verify(userRepositoryMock, times(1)).existsByEmail(userDTO.getEmail());
        
        // No llama al save dado que la excepción no le permitió crear
        verify(userRepositoryMock, times(0)).save(any(UserEntity.class));
    }
    
    
    
    @Test
    void testGetUserByToken() {
    	
        // Mocking the UserRepository
    	String existingEmail = "jose.cavieres2@gmail.com";
    	
    	String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb3NlLmNhdmllcmVzMkBnbWFpbC5jb20iLCJpYXQiOjE3MDEyOTIyODgsImV4cCI6MTcwMTM3ODY4OH0.5IB6aep4moVQZodHOYlFI75_LNWjP1FmXTovK-7o3M";
    	
    	// Usuario existente
        UserEntity existingUser = new UserEntity(); // Create a sample saved user

        existingUser.setActive(true);
        existingUser.setName("John Doe");
        existingUser.setEmail(existingEmail);
        existingUser.setPassword(CommonUtils.encodePassword("Testbci23"));
                
        // responder con el mock
        when(userRepositoryMock.findByEmail(any())).thenReturn(existingUser);
        
        // responde el mock con el mismo usuario
        when(userRepositoryMock.save(any())).thenReturn(existingUser);
        

        // Calling the createUser method
        
        UserEntity usuarioObtenido = userService.getUserByToken(token);
        
        assertNotNull(usuarioObtenido);
        
        // Verify that the findByEmail method was called at least once
        verify(userRepositoryMock, times(1)).findByEmail(any());
        
        // Verify that save was called with the modified user entity
        verify(userRepositoryMock, times(1)).save(usuarioObtenido);
        
        // El correo corresponde
        assertEquals(existingEmail, usuarioObtenido.getEmail());
    }
    
    
    @Test
    void testGetUserByToken_UserNotFoundException() {
    	
    	 // Arrange
        String nonExistingToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb3NlLmNhdmllcmVzMkBnbWFpbC5jb20iLCJpYXQiOjE3MDEyOTIyODgsImV4cCI6MTcwMTM3ODY4OH0.5IB6aep4moVQZodHOYlFI75_LNWjP1FmXTovK-7o3M";
        String email = "nonexistinguser@example.com";

        
        when(jwtUtilsMock.extractSubject(nonExistingToken)).thenReturn(email);
        when(userRepositoryMock.findByEmail(email)).thenReturn(null);
        
        // Se asegura que la excepción se ha lanzado
        assertThrows(UserNotFoundException.class, () -> userService.getUserByToken(nonExistingToken));    
    }
    
    
    
    @Test
    void testConvertRequestToUserEntity() {
    	
        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setName("John");
        signUpRequestDTO.setEmail("john@example.com");
        signUpRequestDTO.setPassword("Tcc21aa$");
        signUpRequestDTO.setPhones(Arrays.asList(new PhoneDTO(123456789, 1, "US"), new PhoneDTO(987654321, 2, "CA"), new PhoneDTO(555555555, 3, "UK")));
        

        // Act
        UserEntity result = userService.convertRequestToUserEntity(signUpRequestDTO);

        // Assert
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
        
        // Assert contiene la misma cantidad de teléfonos solicitados para crear        
        assertListSize(result.getPhones(), 3); 
 
    }
    

    public static void assertListSize(List<?> list, int expectedSize) {
        Assertions.assertEquals(expectedSize, list.size());
    }


}