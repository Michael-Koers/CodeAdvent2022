import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {

        String file = "input-test.txt";

        Map<String, String> monkeys = parseInput(file);

        // Puzzle 1
        String rootMonkey = "root";
        long number = determineMonkeyNumber(rootMonkey, monkeys);
        System.out.printf("Monkey %s yelled %s%n", rootMonkey, number);

        // Puzzle 2
        String human = "humn";
        long humanNumber = reverseEngineerHumanNumber(rootMonkey, human, monkeys);
        System.out.printf("Human %s yelled %s%n", human, humanNumber);
    }

    private static long reverseEngineerHumanNumber(String rootMonkey, String human, Map<String, String> monkeys) {
        String monkeyLeft = monkeys.get(rootMonkey).split(" ")[0].trim();
        String monkeyRight = monkeys.get(rootMonkey).split(" ")[2].trim();

        // Subtract monkeyleft from monkeyright to determine difference
        // Only monkey-left use human!
        monkeys.put(rootMonkey, monkeyLeft + " - " + monkeyRight);

        long min = 0;
        long max = Long.MAX_VALUE;
        long mid = 0;

        long valueLeft = determineMonkeyNumber(monkeyLeft, monkeys);
        long valueRight = determineMonkeyNumber(monkeyRight, monkeys);
        long diff = valueRight-valueLeft;

        // Binary Search
        while (diff != 0){
            mid = (min + max) / 2;
            monkeys.put(human, String.valueOf(mid));

            valueLeft = determineMonkeyNumber(monkeyLeft, monkeys);
            diff = valueLeft - valueRight;

            // changing - steady
            if(diff < 0){
                min = mid;
            } else if (diff > 0){
                max = mid-1;
            }
        }
        return mid;
    }

    private static long determineMonkeyNumber(String monkey, Map<String, String> monkeys) {

        String monkeyValue = monkeys.get(monkey);

        try {
            return Long.parseLong(monkeyValue);
        } catch (NumberFormatException e) {
            String monkey1 = monkeyValue.split(" ")[0].trim();
            String monkey2 = monkeyValue.split(" ")[2].trim();

            switch (monkeyValue.split(" ")[1].trim()) {
                case "-" -> {
                    return determineMonkeyNumber(monkey1, monkeys) - determineMonkeyNumber(monkey2, monkeys);
                }
                case "+" -> {
                    return determineMonkeyNumber(monkey1, monkeys) + determineMonkeyNumber(monkey2, monkeys);
                }
                case "*" -> {
                    return determineMonkeyNumber(monkey1, monkeys) * determineMonkeyNumber(monkey2, monkeys);
                }
                case "/" -> {
                    return determineMonkeyNumber(monkey1, monkeys) / determineMonkeyNumber(monkey2, monkeys);
                }
            }
        }

        return 0;
    }


    private static Map<String, String> parseInput(String file) throws Exception {

        Map<String, String> monkeys = new HashMap<>();

        for (String line : Files.readAllLines(Paths.get("day-21/src/main/resources/" + file))) {
            monkeys.put(line.split(":")[0], line.split(":")[1].trim());
        }
        return monkeys;
    }
}
