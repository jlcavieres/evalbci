package cl.bci.evaluacion.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErroresResponseDTO {	
	
	@JsonProperty("error")
	private List<ErrorResponseDTO> errores;
	
	public List<ErrorResponseDTO> getErrores() {
		return errores;
	}

	public void setErrores(List<ErrorResponseDTO> errores) {
		this.errores = errores;
	}

}
