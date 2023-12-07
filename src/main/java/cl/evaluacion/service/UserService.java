package cl.evaluacion.service;

import cl.bci.evaluacion.dto.SignUpRequestDTO;
import cl.bci.evaluacion.entity.UserEntity;

public interface UserService {

	UserEntity createUser(SignUpRequestDTO userDTO);

	/**
	 * 
	 * @param token puro, sin prefijos
	 */
	UserEntity getUserByToken(String token);

}