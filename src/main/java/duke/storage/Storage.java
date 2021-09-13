package duke.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;



public class Storage {
    public static final String DIRECTORY = "./data";
    private final String fileName;

    public Storage(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the list of tasks from the saved text file.
     *
     * @return List of Tasks.
     */
    public List<Task> load() {
        String path = DIRECTORY + "/" + fileName;
        File file = new File(path);
        List<Task> loadedTasks = new ArrayList<Task>();

        if (!file.exists()) {
            return loadedTasks;
        }

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String type = Character.toString(line.charAt(line.indexOf("[") + 1));
                boolean isDone = Character.toString(line.charAt(line.indexOf("[", 2) + 1)).equals("X");

                int endOfNameIdx = line.contains("(") ? line.indexOf("(") : line.length();
                String name = line.substring(line.indexOf("]",
                        line.indexOf("]") + 1) + 2, endOfNameIdx).stripTrailing();

                String tags = line.contains("tags") ? line.substring(line.indexOf("tags:"), line.indexOf(")")) : "";
                List<String> tagsList = parseTags(tags);

                if (type.equals("T")) {
                    loadedTasks.add(new ToDo(name, isDone, tagsList));
                } else if (type.equals("D")) {
                    name = name.split("\\(")[0].stripTrailing();
                    String parsedInput = line.split("deadline:")[1];
                    String deadline = parsedInput.substring(1, parsedInput.length() - 1);
                    loadedTasks.add(new Deadline(name, isDone, tagsList,
                            LocalDate.parse(deadline, Deadline.DATE_TIME_FORMATTER)));
                } else {
                    name = name.split("\\(")[0].stripTrailing();
                    String parsedInput = line.split("at:")[1];
                    String at = parsedInput.substring(1, parsedInput.length() - 1);
                    loadedTasks.add(new Event(name, isDone, tagsList, at));
                }

                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadedTasks;
    }

    public List<String> parseTags (String tags) {
        String[] splitTags = tags.split("#");
        return Arrays.stream(Arrays.copyOfRange(splitTags, 1, splitTags.length))
                .map(String::stripTrailing).collect(Collectors.toList());
    }

    /**
     * Returns true if file does not exist and false otherwise.
     *
     * @param fileName File name where task data is stored.
     * @return true if file does not exist.
     * @throws IOException if an I/O error occurred.
     */
    public boolean createFileIfNotExists(String fileName) throws IOException {
        File fileDirectory = new File(DIRECTORY);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdir();
        }

        File file = new File(DIRECTORY + "/" + fileName);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }
}
