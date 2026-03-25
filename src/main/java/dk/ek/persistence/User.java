package dk.ek.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements ISecurityUser{
    @Id
    @Column(name = "user_name", nullable = false)
    private String userName;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles = new HashSet<>();
    public User(String username, String password ){
        String salt = BCrypt.gensalt(12);
        String hashedPassword = BCrypt.hashpw(password, salt);
        this.userName = username;
        this.password = hashedPassword;
    }


    @Override
    public Set<String> getRolesAsStrings() {
        return this.roles.stream().map((role)->role.getRoleName()).collect(Collectors.toSet());
    }

    @Override
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    @Override
    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public void removeRole(String role) {

    }
}