package informaciska.com.ToDo;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean upper = false, lower = false, digit = false, special = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digit = true;
            else special = true;
        }
        return upper && lower && digit && special;
    }

    // Дел од UserService.java

    public boolean startRegistration(String username, String password, String email) {
        if (!isStrongPassword(password)) return false;
        if (userRepository.findByUsername(username).isPresent()) return false;

        String salt = PasswordUtil.generateSalt();
        String hashed = PasswordUtil.hashPassword(password, salt);

        User newUser = new User(username, hashed, email, salt, "GUEST");

        String code = PasswordUtil.generate6DigitCode();
        newUser.setVerificationCode(code);
        newUser.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));
        newUser.setVerified(false);

        userRepository.save(newUser);

        emailService.sendVerificationCode(email, code);
        return true;
    }

    public boolean verifyRegistrationCode(String username, String code) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isEmpty()) return false;
        User user = u.get();
        if (user.getVerificationCode() == null) return false;
        if (user.getVerificationExpiry() == null) return false;
        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) return false;
        if (!user.getVerificationCode().equals(code)) return false;
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);
        return true;
    }

    public boolean startLogin2FA(String username, String password) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isEmpty()) return false;
        User user = u.get();
        String hashed = PasswordUtil.hashPassword(password, user.getSalt());
        if (!hashed.equals(user.getHashedPassword())) return false;
        if (!user.isVerified()) return false;
        String code = PasswordUtil.generate6DigitCode();
        user.setVerificationCode(code);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        emailService.sendVerificationCode(user.getEmail(), code);
        return true;
    }

    public boolean verifyLogin2FA(String username, String code) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isEmpty()) return false;
        User user = u.get();
        if (user.getVerificationCode() == null) return false;
        if (user.getVerificationExpiry() == null) return false;
        if (LocalDateTime.now().isAfter(user.getVerificationExpiry())) return false;
        if (!user.getVerificationCode().equals(code)) return false;
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);
        return true;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
