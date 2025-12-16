import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PasswordGenerator <password>");
            return;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(args[0]);
        System.out.println("Hashed password: " + hashedPassword);
    }
}
