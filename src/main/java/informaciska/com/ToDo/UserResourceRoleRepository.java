package informaciska.com.ToDo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserResourceRoleRepository extends JpaRepository<UserResourceRole, Long> {

    List<UserResourceRole> findByUsernameAndResourceTypeAndResourceRoleId(
            String username,
            String resourceType,
            String resourceRoleId
    );

    List<UserResourceRole> findByUsername(String username);
}