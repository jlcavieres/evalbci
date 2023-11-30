package cl.bci.evaluacion.dto;

import java.util.Date;

public class SignUpResponseDTO {
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Long id; // Puede ser un UUID, así que podría ser de tipo String
    private Date created;
    private Date lastLogin;
    private String token;
    private boolean isActive;

    // Constructores, getters y setters

    // Constructor por defecto
    public SignUpResponseDTO() {
    }



    // Getters y setters

  

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
