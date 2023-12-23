package pw.react.backend.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

    import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username;
    @Column
    private String firstname;
    @Column
    private String lastname;
    @Column
    private Long balance;// w groszach/centach
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private Boolean isAdmin;
    @Column Long DistanceTravelled; //w metrach

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public long getBalance() {
        return balance;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Long getDistanceTravelled() {
        return DistanceTravelled;
    }

    public void setDistanceTravelled(Long distanceTravelled) {
        DistanceTravelled = distanceTravelled;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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
