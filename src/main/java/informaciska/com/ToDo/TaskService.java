package informaciska.com.ToDo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Треба да се увезе
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasks(String user) {
        return taskRepository.findByUsername(user);
    }

    @Transactional
    public void addTask(String user, String desc) {
        Task newTask = new Task(desc, user);
        taskRepository.save(newTask); // Оваа операција бара трансакција
    }

    @Transactional
    public void deleteTask(String user, Long id) {
        taskRepository.findById(id).ifPresent(task -> {

            taskRepository.delete(task);

        });
    }
}