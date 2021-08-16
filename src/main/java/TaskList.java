import java.util.List;
import java.util.ArrayList;

public class TaskList {
    private final List<Task> list;

    TaskList() {
        this.list = new ArrayList<Task>();
    }

    TaskList(List<Task> storage) {
        this.list = storage;
    }

    List<Task> getList() {
        return this.list;
    }

    Task getTask(int index) {
        return list.get(index - 1);
    }

    void markTaskAsDone(int index) {
        Task doneTask = getTask(index).markAsDone();
        list.set(index - 1, doneTask);
        System.out.println("Nice! I've marked this task as done:");
        System.out.printf("    %s%n", doneTask.toString());
    }

    void add(Task item) {
        list.add(item);
    }

    void listItems() {
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d. %s%n", (i + 1), list.get(i));
        }
    }


}
