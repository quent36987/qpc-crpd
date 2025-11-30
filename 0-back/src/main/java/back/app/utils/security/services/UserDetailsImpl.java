package back.app.utils.security.services;

import back.app.data.model.EDroit;
import back.app.data.model.ERole;
import back.app.data.model.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;
    @Getter
    private String nom;
    @Getter
    private String prenom;
    @Getter
    private String email;
    @Getter
    private String tel;

    @JsonIgnore
    private String password;

    @Getter
    private ERole role;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           String nom,
                           String prenom,
                           String email,
                           String tel,
                           String password,
                           ERole role,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.tel = tel;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserModel user) {
        Collection<GrantedAuthority> authorities = user.getRole().getDroits().stream()
                .map(EDroit::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getTel(),
                user.getPassword(),
                user.getRole(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // on utilise l'email comme "username"
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}