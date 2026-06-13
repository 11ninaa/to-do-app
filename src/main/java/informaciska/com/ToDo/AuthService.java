package informaciska.com.ToDo;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserResourceRoleRepository userResourceRoleRepository;

    public AuthService(UserRepository userRepository, UserResourceRoleRepository userResourceRoleRepository) {
        this.userRepository = userRepository;
        this.userResourceRoleRepository = userResourceRoleRepository;
    }

    public boolean hasOrganizationalRole(String username, String requiredRole) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isEmpty()) return false;

        return u.get().getOrganizationRole() != null && u.get().getOrganizationRole().equals(requiredRole);
    }

    public UserResourceRole grantJitRole(String username, String resourceType, String roleId, long durationMinutes) {

        UserResourceRole jitRole = new UserResourceRole();
        jitRole.setUsername(username);
        jitRole.setResourceType(resourceType);
        jitRole.setResourceRoleId(roleId);
        jitRole.setGrantedAt(LocalDateTime.now());
        jitRole.setExpiresAt(LocalDateTime.now().plusMinutes(durationMinutes));
        jitRole.setActive(true);

        return userResourceRoleRepository.save(jitRole);
    }

    public boolean hasActiveResourceRole(String username, String resourceType, String requiredRoleId) {
        List<UserResourceRole> roles = userResourceRoleRepository
                .findByUsernameAndResourceTypeAndResourceRoleId(username, resourceType, requiredRoleId);

        boolean accessGranted = false;

        for (UserResourceRole role : roles) {
            if (role.isActive()) {
                if (role.isExpired()) {
                    role.setActive(false);
                    userResourceRoleRepository.save(role);
                } else {
                    accessGranted = true;
                    break;
                }
            }
        }
        return accessGranted;
    }
}
