package it.macgood.authjwt.user;

import com.fasterxml.jackson.annotation.JsonView;
import it.macgood.authjwt.views.View;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @JsonView(View.GetProfileInfo.class)
    private Integer id;

    @JsonView(View.GetProfileInfo.class)
    private String firstname;

    @JsonView(View.GetProfileInfo.class)
    private String lastname;

    @JsonView(View.GetProfileInfo.class)
    private String nickname;

    @JsonView(View.GetProfileInfo.class)
    @Column(unique = true)
    private String email;

    @JsonView(View.GetProfileInfo.class)
    private String photo;

    private String password;

    @JsonView(View.GetProfileInfo.class)
    private String dateOfBirth;

    private String currentToken;

    @JsonView(View.GetProfileInfo.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Career> career;

    @JsonView(View.GetProfileInfo.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    @JsonView(View.GetProfileInfo.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Works> works;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
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
}
