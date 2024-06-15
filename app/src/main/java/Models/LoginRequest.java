package Models;

public class LoginRequest {
    private String username;
    private String userpassword;
    private String ipAddress;

    public LoginRequest(String username, String userpassword, String ipAddress) {
        this.username = username;
        this.userpassword = userpassword;
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
