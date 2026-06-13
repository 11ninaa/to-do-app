package informaciska.com.ToDo;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model,
                               @RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "email", required = false) String email) {
        model.addAttribute("oldUsername", username);
        model.addAttribute("oldEmail", email);
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model) {

        boolean ok = userService.startRegistration(username, password, email);

        if (!ok) {
            model.addAttribute("error", "Слаба лозинка или корисникот веќе постои!");
            model.addAttribute("oldUsername", username);
            model.addAttribute("oldEmail", email);
            return "register";
        }

        model.addAttribute("info", "Испратен е код на е-пошта. Внесете го за да ја потврдите регистрацијата.");
        model.addAttribute("username", username);
        return "verify";
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam(value = "username", required = false) String username, Model model) {
        model.addAttribute("username", username);
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String username, @RequestParam String code, Model model) {
        boolean ok = userService.verifyRegistrationCode(username, code);
        if (!ok) {
            model.addAttribute("error", "Невалиден или истечен код. Обиди се повторно.");
            model.addAttribute("username", username);
            return "verify";
        }
        model.addAttribute("info", "Регистрацијата е потврдена. Може да се најавите.");
        return "login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Невалидно корисничко име или лозинка.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {

        boolean ok = userService.startLogin2FA(username, password);
        if (!ok) {
            return "redirect:/login?error";
        }
        request.getSession().setAttribute("preAuthUser", username);
        return "redirect:/twofa";
    }

    @GetMapping("/twofa")
    public String twofaPage() {
        return "twofa";
    }

    @PostMapping("/twofa")
    public String twofa(@RequestParam String code, HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("preAuthUser");
        if (username == null) {
            return "redirect:/login";
        }
        boolean ok = userService.verifyLogin2FA(username, code);
        if (!ok) {
            model.addAttribute("error", "Невалиден или истечен код.");
            return "twofa";
        }
        request.getSession().removeAttribute("preAuthUser");
        request.getSession().setAttribute("currentUser", username);
        return "redirect:/todo";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }
}