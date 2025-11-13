package co.com.nequi.r2dbc;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "users")
@Entity
@Data
public class UserEntity {

    @Id
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;

}
