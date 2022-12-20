import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws Exception {

        String file = "input.txt";

        List<Integer> encryptedFile = parseInput(file);

        // puzzle 1
        int sumGroveCoords = decryptFile(encryptedFile);

        System.out.printf("Sum grove coords: %s%n", sumGroveCoords);
    }

    private static int decryptFile(List<Integer> encryptedFile) {
        List<Integer> clone = List.copyOf(encryptedFile);

        // Time to get schwifty
        for (Integer digit : clone) {
            int curIndex = encryptedFile.indexOf(digit);
            int newIndex = 0;
            if(digit > 0) {
                newIndex = (curIndex + digit) % (encryptedFile.size()-1);
            } else if (digit < 0){
                newIndex = Math.abs(encryptedFile.size()-1 + (curIndex + digit)) % (encryptedFile.size());
            } else {
                continue;
            }

            encryptedFile.remove(curIndex);
            encryptedFile.add(newIndex, digit);
        }

        int startPos = encryptedFile.indexOf(0);
        int one_thousand = encryptedFile.get((1000 + startPos) % encryptedFile.size());
        int two_thousand = encryptedFile.get((2000 + startPos) % encryptedFile.size());
        int three_thousand = encryptedFile.get((3000 + startPos) % encryptedFile.size());

        return one_thousand + two_thousand + three_thousand;
    }

    private static List<Integer> parseInput(String file) throws Exception {

        List<Integer> encryptedFile = new ArrayList<Integer>();
        for (String line : Files.readAllLines(Paths.get("day-20/src/main/resources/" + file))) {
            encryptedFile.add(Integer.parseInt(line));
        }

        return encryptedFile;
    }
}
