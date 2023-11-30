package cl.bci.evaluacion.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SignUpRequestDTO {

	private String name;

	// @NotNull(message = "Email no pueder ser nulo")
	@Email(message = "Formato de email incorrecto")
	private String email;

	// @NotNull(message = "Password no puede ser nulo")
	@Pattern(regexp = "^(?=(.*[A-Z]){1})(?=(.*\\d){2})[a-zA-Z\\d]{8,12}$", message = "Password debe tener solo una mayúscula y dos números, con un largo máximo de 12 y mínimo 8.")
	private String password;

	public void setPhones(List<PhoneDTO> phones) {
		this.phones = phones;
	}

	private List<PhoneDTO> phones;

	//
	// Getters and Setters

	public List<PhoneDTO> getPhones() {
		return phones;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
