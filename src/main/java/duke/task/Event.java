package duke.task;

public class Event extends Task{
    protected final String time;

    public Event(String name, String time) {
        super(name);
        this.time = time;
    }

    public Event(String name, boolean isDone, String time) {
        super(name, isDone);
        this.time = time;
    }

    @Override
    public Event markAsDone() {
        return new Event(super.getName(), true, time);
    }

    @Override
    public String toString() {
        return String.format("[E]%s (at: %s)", super.toString(), time);
    }
}