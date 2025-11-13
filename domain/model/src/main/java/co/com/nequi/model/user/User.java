package co.com.nequi.model.user;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
}
