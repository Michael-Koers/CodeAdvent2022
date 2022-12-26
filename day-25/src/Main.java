import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        List<String> snafus = parseInput("input.txt");

        List<Long> decimals = parseSnafu(snafus);
        long total = decimals.stream().reduce(Long::sum).orElseThrow();
        String snafu = parseDecimal(total);

        System.out.printf("We need to input: %s%n", snafu);
    }

    private static String parseDecimal(long total) {
        StringBuilder snafu = new StringBuilder();
        while (total > 0) {
            long fives = (total + 2) / 5;
            int digit = (int) (total - 5 * fives);
            snafu.insert(0, decimalToSnafu(digit));
            total = fives;
        }
        return snafu.toString();
    }

    private static List<Long> parseSnafu(List<String> snafu) {
        List<Long> decimals = new ArrayList<>();
        for (String line : snafu) {
            long result = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                result = result * 5 + snafuToDecimal(c);
            }
            decimals.add(result);
        }
        return decimals;
    }

    private static char decimalToSnafu(int d) {
        return switch (d) {
            case 2 -> '2';
            case 1 -> '1';
            case 0 -> '0';
            case -1 -> '-';
            case -2 -> '=';
            default -> throw new IllegalArgumentException();
        };
    }

    private static long snafuToDecimal(char c) {
        return switch (c) {
            case '2' -> 2;
            case '1' -> 1;
            case '0' -> 0;
            case '-' -> -1;
            case '=' -> -2;
            default -> throw new IllegalArgumentException();
        };
    }

    private static List<String> parseInput(String s) throws IOException {
        return Files.readAllLines(Paths.get("day-25/" + s));
    }
}
