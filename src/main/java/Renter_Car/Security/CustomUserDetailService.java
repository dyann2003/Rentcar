package Renter_Car.Security;

import Renter_Car.Models.AuthUser;
import Renter_Car.Models.Role;
import Renter_Car.Repository.RoleRepository;
import Renter_Car.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Renter_Car.Models.User account = userRepository.findAccountByEmail(email);

        if (account != null) {
            Set<Role> roles = account.getRoles();
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            if (roles != null) {
                grantedAuthorities = roles
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
            }

            return new AuthUser(
                    account.getFullName(),
                    account.getPassword(),
                    grantedAuthorities,
                    account.getId(),
                    account.getCreateAt(),
                    account.getIsDelete()
            );

        } else {
            throw new UsernameNotFoundException("Invalid email or password");
        }
    }
}
