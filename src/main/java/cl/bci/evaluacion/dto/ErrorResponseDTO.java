package cl.bci.evaluacion.dto;

import java.sql.Timestamp;


public class ErrorResponseDTO {	
	
    private Timestamp timestamp;
    private int codigo;
    private String detail;

    // Constructors, getters, and setters

    // Default constructor
    public ErrorResponseDTO() {
    }

    // Parameterized constructor
    public ErrorResponseDTO(Timestamp timestamp, int codigo, String detail) {
        this.timestamp = timestamp;
        this.codigo = codigo;
        this.detail = detail;
    }

    // Getters and Setters

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}