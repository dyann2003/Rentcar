package Renter_Car.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
public class AuthUser extends User {
    private int id;
    private Date createAt;
    private boolean isDeleted;

    public AuthUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            int id,
            Date createAt,
            boolean isDeleted
    ) {
        super(username, password, authorities);
        this.id = id;
        this.createAt = createAt;
        this.isDeleted = isDeleted;
    }
}
