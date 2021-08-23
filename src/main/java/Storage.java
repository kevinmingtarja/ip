import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    public static final String directory = "./data";
    private final String fileName;

    public Storage(String fileName) {
        this.fileName = fileName;
    }

    List<Task> load() {
        String path = directory + "/" + fileName;
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
                String name = line.substring(line.indexOf("]", line.indexOf("]") + 1) + 2);

                if (type.equals("T")) {
                    loadedTasks.add(new ToDo(name, isDone));
                } else if (type.equals("D")) {
                    name = name.split("\\(")[0].stripTrailing();
                    String parsedInput = line.split("deadline:")[1];
                    String deadline = parsedInput.substring(1, parsedInput.length() - 1);
                    loadedTasks.add(new Deadline(name, isDone, LocalDate.parse(deadline, Deadline.formatter)));
                } else {
                    name = name.split("\\(")[0].stripTrailing();
                    String parsedInput = line.split("at:")[1];
                    String at = parsedInput.substring(1, parsedInput.length() - 1);
                    loadedTasks.add(new Event(name, isDone, at));
                }

                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadedTasks;
    }

    boolean createFileIfNotExists(String fileName) throws IOException {
        File fileDirectory = new File(directory);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdir();
        }

        File file = new File(directory + "/" + fileName);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }
}
