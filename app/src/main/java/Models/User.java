package Models;


public class User {

    private static int id;
    private static String username;
    private static String userpassword;
    private static String email;
    private static String nombre;

    private static boolean cuentaBloqueada;

    public static int getId() {
        return id;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getUserpassword() {
        return userpassword;
    }

    public static void setUserpassword(String userpassword) {
        User.userpassword = userpassword;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        User.nombre = nombre;
    }

    public static void setId(int id) {
        User.id = id;
    }
}
