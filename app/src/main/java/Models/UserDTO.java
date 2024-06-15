package Models;


public class UserDTO {

    private int id;
    private String username;
    private String userpassword;
    private String email;
    private String nombre;

    private boolean cuentaBloqueada;

    public UserDTO() {
    }

    public UserDTO(int id, String username, String userpassword, String email, String nombre, boolean cuentaBloqueada) {
        this.id = id;
        this.username = username;
        this.userpassword = userpassword;
        this.email = email;
        this.nombre = nombre;
        this.cuentaBloqueada = cuentaBloqueada;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCuentaBloqueada() {
        return cuentaBloqueada;
    }

    public void setCuentaBloqueada(boolean cuentaBloqueada) {
        this.cuentaBloqueada = cuentaBloqueada;
    }

    public int getId() {
        return id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
