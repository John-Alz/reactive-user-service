package co.com.nequi.redis.template.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UserCache {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;

}
