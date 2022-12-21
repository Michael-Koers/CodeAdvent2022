import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) throws Exception {

        String file = "input-test.txt";

        // puzzle 1
        List<Digit> encryptedFile = parseInput(file);
        decryptFile(encryptedFile, List.copyOf(encryptedFile));
        long sum = calculateCoordsSum(encryptedFile);
        System.out.printf("Puzzle 1 - Sum grove coords: %s%n", sum);

        // Puzzle 2
        List<Digit> encryptedFile2 = parseInput(file);
        long decreptionKey = 811589153L;
        encryptedFile2.forEach(d -> d.number *= decreptionKey);
        decryptFileNTimes(10, encryptedFile2, List.copyOf(encryptedFile2));
        long sum2 = calculateCoordsSum(encryptedFile2);
        System.out.printf("Puzzle 2 - Sum grove coords: %s%n", sum2);
    }

    private static void decryptFileNTimes(int times, List<Digit> encryptedFile, List<Digit> decryptionOrder) {
        for(int i = 0; i < times; i++){
            decryptFile(encryptedFile,decryptionOrder);
        }
    }

    private static long calculateCoordsSum(List<Digit> encryptedFile) {
        Digit startDigit = encryptedFile.stream().filter(d -> d.number == 0).findFirst().orElseThrow(NoSuchElementException::new);
        int startPos = encryptedFile.indexOf(startDigit);
        long one_thousand = encryptedFile.get((1000 + startPos) % encryptedFile.size()).number;
        long two_thousand = encryptedFile.get((2000 + startPos) % encryptedFile.size()).number;
        long three_thousand = encryptedFile.get((3000 + startPos) % encryptedFile.size()).number;

        return one_thousand + two_thousand + three_thousand;
    }

    private static List<Digit> decryptFile(List<Digit> encryptedFile, List<Digit> decryptionOrder) {

        System.out.printf("%s%n", encryptedFile.stream().map(x -> String.valueOf(x.number)).collect(Collectors.joining(", ")));

        // Time to get schwifty
        for (Digit digit : decryptionOrder) {
            if (digit.number == 0) {
                continue;
            }

            int mod = encryptedFile.size() - 1;
            int curIndex = encryptedFile.indexOf(digit);

            Digit tmp = encryptedFile.remove(curIndex);
            int newIndex = Math.floorMod(curIndex + digit.number, mod);
            encryptedFile.add(newIndex, tmp);

            String list = encryptedFile.stream().map(x -> String.valueOf(x.number)).collect(Collectors.joining(", "));
            System.out.printf("%s%n", list);
        }
        return encryptedFile;

    }

    private static List<Digit> parseInput(String file) throws Exception {

        List<Digit> encryptedFile = new ArrayList<Digit>();
        for (String line : Files.readAllLines(Paths.get("day-20/src/main/resources/" + file))) {
            encryptedFile.add(new Digit(Integer.parseInt(line)));
        }

        return encryptedFile;
    }
}

class Digit {
    long number;

    public Digit(int number) {
        this.number = number;
    }
}