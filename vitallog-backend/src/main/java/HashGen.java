
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "test1234";
        String encoded = encoder.encode(raw);
        System.out.println("HASH_START|" + encoded + "|HASH_END");
    }
}
