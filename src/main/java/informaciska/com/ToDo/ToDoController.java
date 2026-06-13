package informaciska.com.ToDo;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ToDoController {

    private final TaskService taskService;
    private final AuthService authService;
    private final UserService userService;

    public ToDoController(TaskService taskService, AuthService authService, UserService userService) {
        this.taskService = taskService;
        this.authService = authService;
        this.userService = userService;
    }

    public String getCurrentUser(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("currentUser");
    }

    @GetMapping("/todo")
    public String todoPage(Model model, HttpServletRequest request) {
        String user = getCurrentUser(request);
        if (user == null) return "redirect:/login";

        model.addAttribute("tasks", taskService.getTasks(user));

        boolean canWrite = authService.hasActiveResourceRole(user, "TASKS", "TASK_WRITER");
        model.addAttribute("canWrite", canWrite); // За прикажување во todohtml

        if (request.getParameter("accessError") != null) {
            String errorMsg = "";
            switch (request.getParameter("accessError")) {
                case "NO_WRITE_PRIVILEGE":
                    errorMsg = "Немате активна дозвола за додавање задачи. Побарајте JIT пристап.";
                    break;
                case "ADMIN_REQUIRED":
                    errorMsg = "За бришење е потребна администраторска улога.";
                    break;
                default:
                    errorMsg = "Пристапот е одбиен.";
            }
            model.addAttribute("error", errorMsg);
        }

        if (request.getParameter("jit_success") != null) {
            model.addAttribute("info", "JIT пристапот е успешно доделен на 10 минути!");
        }

        return "todohtml";
    }
    @PostMapping("/todo/add")
    public String addTask(@RequestParam String description, HttpServletRequest request) {
        String user = getCurrentUser(request);
        if (user == null) return "redirect:/login";

        if (!authService.hasActiveResourceRole(user, "TASKS", "TASK_WRITER")) {
            return "redirect:/todo?accessError=NO_WRITE_PRIVILEGE";
        }

        taskService.addTask(user, description);
        return "redirect:/todo";
    }

    @GetMapping("/todo/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpServletRequest request) {
        String user = getCurrentUser(request);
        if (user == null) return "redirect:/login";

        if (!authService.hasOrganizationalRole(user, "ADMIN")) {
            return "redirect:/todo?accessError=ADMIN_REQUIRED";
        }

        taskService.deleteTask(user, id);
        return "redirect:/todo";
    }


    @GetMapping("/request-access")
    public String requestAccessPage(Model model, HttpServletRequest request) {
        String user = getCurrentUser(request);
        if (user == null) return "redirect:/login";

        boolean canWrite = authService.hasActiveResourceRole(user, "TASKS", "TASK_WRITER");
        model.addAttribute("canWrite", canWrite);

        return "request_access";
    }

    @PostMapping("/request-access")
    public String grantAccess(HttpServletRequest request) {
        String user = getCurrentUser(request);
        if (user == null) return "redirect:/login";

        long durationMinutes = 10;
        authService.grantJitRole(user, "TASKS", "TASK_WRITER", durationMinutes);

        return "redirect:/todo?jit_success";
    }

    @GetMapping("/admin/users")
    public String adminPanel(Model model, HttpServletRequest request) {
        String user = getCurrentUser(request);

        if (user == null || !authService.hasOrganizationalRole(user, "ADMIN")) {
            return "redirect:/todo?accessError=ADMIN_REQUIRED";
        }

        model.addAttribute("allUsers", userService.getAllUsers());

        return "admin_panel";
    }

    @PostMapping("/admin/grant-task-writer")
    public String adminGrantRole(@RequestParam String targetUser, HttpServletRequest request) {
        String adminUser = getCurrentUser(request);

        if (adminUser == null || !authService.hasOrganizationalRole(adminUser, "ADMIN")) {
            return "redirect:/login";
        }

        authService.grantJitRole(targetUser, "TASKS", "TASK_WRITER", 30);

        return "redirect:/admin/users?success=RoleGranted";
    }


}