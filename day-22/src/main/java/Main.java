import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        String file = "input-test.txt";
        Password p = parseInput(file);

        printField(p.field);

    }

    private static void printField(String[][] field) {

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                String c = field[i][j] == null ? "" : field[i][j];
                System.out.printf("%s", c);
            }
            System.out.println("");
        }
    }

    private static Password parseInput(String file) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("day-22/src/main/resources/" + file));

        int maxLineLength = lines.stream()
                // Drop last 2 lines to get accurate dimensions of field
                .limit(lines.size() - 2L)
                .map(l -> l.split(""))
                .mapToInt(l -> l.length)
                .max()
                .orElseThrow(IOException::new);

        List<String> steps = new ArrayList<>();
        String[][] field = new String[lines.size()][maxLineLength];

        int index = 0;
        // Read map
        while (!lines.get(index).isBlank()) {
            field[index] = lines.get(index).split("");
            index++;
        }

        // Last line of input are the steps
        steps.addAll(List.of(lines.get(lines.size() - 1).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")));

        return new Password(steps, field);
    }


}


class Password {

    final List<String> steps;
    final String[][] field;

    Password(List<String> steps, String[][] field) {
        this.steps = steps;
        this.field = field;
    }
}
