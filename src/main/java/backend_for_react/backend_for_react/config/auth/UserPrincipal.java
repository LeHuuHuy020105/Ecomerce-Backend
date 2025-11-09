package backend_for_react.backend_for_react.config.auth;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.enums.UserStatus;
import backend_for_react.backend_for_react.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // add role với prefix ROLE_
        authorities.addAll(user.getRoles().stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName())
                .collect(Collectors.toSet()));

        // add permission
        authorities.addAll(user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> (GrantedAuthority) () -> permission.getName())
                .collect(Collectors.toSet()));

        return authorities;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE; // tùy enum Status bạn dùng
    }
}
