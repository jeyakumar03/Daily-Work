package source.model;

public class Passenger {
    private String name;
    private String gender;
    private String mobile;
    private String email;
    public Passenger(String name, String gender, String mobile, String email) {
        this.name = name;
        this.gender = gender;
        this.mobile = mobile;
        this.email = email;
    }
    public Passenger(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.mobile = null;
        this.email = null;
    }
    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }
}
