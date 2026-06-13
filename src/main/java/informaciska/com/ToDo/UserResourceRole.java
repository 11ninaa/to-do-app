package informaciska.com.ToDo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_resource_roles")
public class UserResourceRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String resourceType;
    private String resourceRoleId;

    private LocalDateTime grantedAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    public UserResourceRole() {}

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceRoleId() { return resourceRoleId; }
    public void setResourceRoleId(String resourceRoleId) { this.resourceRoleId = resourceRoleId; }
    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}