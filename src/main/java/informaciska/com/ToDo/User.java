
package informaciska.com.ToDo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;
    private String hashedPassword;
    private String salt;
    private String email;
    private boolean verified;
    private String verificationCode;
    private LocalDateTime verificationExpiry;

    private String organizationRole;

    public User() {}

    public User(String username, String hashedPassword, String email, String salt, String organizationRole) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.salt = salt;
        this.verified = false;
        this.organizationRole = organizationRole;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public LocalDateTime getVerificationExpiry() { return verificationExpiry; }
    public void setVerificationExpiry(LocalDateTime verificationExpiry) { this.verificationExpiry = verificationExpiry; }

    public String getOrganizationRole() { return organizationRole; }
    public void setOrganizationRole(String organizationRole) { this.organizationRole = organizationRole; }
}