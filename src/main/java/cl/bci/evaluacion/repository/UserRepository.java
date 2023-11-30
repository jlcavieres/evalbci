package cl.bci.evaluacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.bci.evaluacion.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	public Boolean existsByEmail(String email);	  
	
	// public List<UserEntity> findByEmail(String emailAddress);
	public UserEntity findByEmail(String emailAddress);
}