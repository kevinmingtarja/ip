import java.util.Scanner;

import java.time.LocalDate;

public class Chatbot {
    private final String name;
    private final TaskList taskList;
    private final Ui ui;
    private final Storage storage;

    Chatbot(String name) {
        this.name = name;
        this.ui = new Ui();
        this.storage = new Storage("list.txt");
        this.taskList = new TaskList(storage.load());
    }

    void initialize() {
        greet();
        listen();
    }

    void greet() {
        ui.printDivider();
        ui.print("Hey there! I'm %s%n", name);
        ui.print("How can I help you?");
        ui.printDivider();
    }

    void listen() {
        Scanner sc = new Scanner(System.in);
        String input;
        boolean isListening;

        do {
            input = ui.readFromUser();
            isListening = parseInput(input);
        } while (isListening);
    }

    boolean parseInput(String input) {
        ui.printDivider();
        try {
            if (input.equals("bye")) {
                ui.print("Goodbye human. See you soon!");
                return false;
            } else if (input.equals("list")) {
                printItems();
            } else if (input.contains("done")) {
                markAsDone(input);
            } else if (input.contains("delete")) {
                deleteItem(input);
            } else {
                addItem(input);
            }
        } catch (HAL9000Exception e) {
            ui.print(e.getMessage());
        }

        ui.printDivider();
        return true;
    }

    void addItem(String input) throws HAL9000Exception {
        Task newItem = null;
        if (input.contains("todo")) {
            String[] parsedInput = input.split(" ", 2); // Splits input into array of [todo, ...]
            if (isIncomplete(parsedInput)) {
                throw new ToDoException();
            }

            newItem = new ToDo(parsedInput[1]);
        } else if (input.contains("deadline")) {
            String[] parsedInput = input.split(" ", 2); // Splits input into array of [deadline, ...]
            if (isIncomplete(parsedInput)) {
                throw new DeadlineException();
            }

            String[] description = parsedInput[1].split("/");
            if (isIncomplete(description)) {
                throw new DeadlineException("The deadline cannot be empty. Please provide a deadline for this task.");
            }

            String name = description[0];
            String deadline = description[1].split(" ")[1];
            newItem = new Deadline(name, LocalDate.parse(deadline));
        } else if (input.contains("event")) {
            String[] parsedInput = input.split(" ", 2); // Splits input into array of [event, ...]
            if (isIncomplete(parsedInput)) {
                throw new EventException();
            }

            String[] description = parsedInput[1].split("/");
            if (isIncomplete(description)) {
                throw new EventException("The time of event cannot be empty. Please provide a time for this task.");
            }
            String name = description[0];
            String time = description[1].split(" ", 2)[1];
            newItem = new Event(name, time);
        } else {
            throw new HAL9000Exception("I'm sorry, but I do not quite understand what that means :(");
        }


        taskList.add(newItem);
    }

    void printItems() {
        taskList.printItems();
    }

    void markAsDone(String input) throws HAL9000Exception {
        int index = getIndexFromInput(input);
        taskList.markTaskAsDone(index);
    }

    void deleteItem(String input) throws HAL9000Exception {
        int index = getIndexFromInput(input);
        taskList.delete(index);
    }

    int getIndexFromInput(String input) throws HAL9000Exception {
        String[] parsedInput = input.split(" ");
        if (isIncomplete(parsedInput)) {
            throw new HAL9000Exception("I do not know which task you are referring to. Please provide a number.");
        }
        return Integer.parseInt(parsedInput[1]);
    }

    boolean isIncomplete(String[] arr) {
        return arr.length <= 1;
    }
}
