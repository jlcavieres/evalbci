package cl.bci.evaluacion.dto;

public class PhoneDTO {
    private long number;
    private int citycode;
    private String countrycode;

    // Constructors, getters, and setters

    // Default constructor
    public PhoneDTO() {
    }

    // Parameterized constructor
    public PhoneDTO(long number, int citycode, String countrycode) {
        this.number = number;
        this.citycode = citycode;
        this.countrycode = countrycode;
    }

    // Getters and Setters

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public int getCitycode() {
        return citycode;
    }

    public void setCitycode(int citycode) {
        this.citycode = citycode;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }
}
