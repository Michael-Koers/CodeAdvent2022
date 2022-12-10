import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {

    public static void main(String[] args) throws Exception {

        List<String> lines = Files.readAllLines(Paths.get("input.txt"));

        int registerValue = 1;
        int cycle = 0;
        int lineNumber = 0;

        Map<Integer, Integer> signalStrengthHistory = new HashMap<>();

        while (lineNumber < lines.size()) {

            String[] cmd = lines.get(lineNumber).split(" ");

            switch (cmd[0]) {
                case "noop" -> {
                    drawToCRT(cycle, registerValue);
                    signalStrengthHistory.put(++cycle, registerValue * cycle);
                }
                case "addx" -> {
                    // Fake two cycles, update registervalue at end of the "cycle"
                    drawToCRT(cycle, registerValue);
                    signalStrengthHistory.put(++cycle, registerValue * cycle);

                    drawToCRT(cycle, registerValue);
                    signalStrengthHistory.put(++cycle, registerValue * cycle);

                    registerValue += Integer.parseInt(cmd[1]);
                }
            }
            lineNumber++;
        }

        System.out.println("");

        // Part 1
        int totalStrength = 0;
        for (int i = 20; i <= 220; i+= 40){
            totalStrength += signalStrengthHistory.get(i);
            System.out.printf("Cycle %s: %s%n", i, signalStrengthHistory.get(i));
        }

        System.out.printf("Total strength: %s%n", totalStrength);
    }

    private static void drawToCRT(int cycle, int registerValue) {
        int position = cycle % 40;

        // Determine if line needs start formatting
        if (position == 0) {
            System.out.printf("Cycle %-3s -> ", cycle);
        }

        // Determine what CRT needs to draw
        if (position >= registerValue - 1 && position <= registerValue + 1) {
            System.out.print("#");
        } else {
            System.out.print(".");
        }

        // Determine if line needs EOL formatting
        if (position == 39) {
            System.out.printf(" <- Cycle %s%n", cycle);
        }
    }
}



