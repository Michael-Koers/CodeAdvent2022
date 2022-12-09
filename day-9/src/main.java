import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;

public class main {

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("input.txt"));

        Map<Integer, Integer> positionMap = new HashMap<>();

        Position head = new Position();
        Position tail = new Position();

        System.out.printf("Starting positions head: %s and %s%n", head.x, head.y);
        System.out.printf("Starting positions tail: %s and %s%n", tail.x, tail.y);

        // Include starting position
        positionMap.putIfAbsent(tail.toString().hashCode(), 1);

        for (String line : lines) {

            String[] cmd = line.split(" ");
            int steps = Integer.parseInt(cmd[1]);

            for (int i = 0; i < steps; i++) {

                Position previousPosition = new Position(head.x, head.y);

                switch (cmd[0]) {
                    case "D" -> --head.y;
                    case "U" -> ++head.y;
                    case "L" -> --head.x;
                    case "R" -> ++head.x;
                }

                if (!adjacentPositions(head, tail)) {
                    tail.move(previousPosition);

                    int occurance = positionMap.getOrDefault(tail.toString().hashCode(), 0);
                    positionMap.put(tail.toString().hashCode(), ++occurance);
                }

                System.out.printf("Current position head: %s and %s%n", head.x, head.y);
                System.out.printf("Current position tail: %s and %s%n", tail.x, tail.y);
                System.out.println("---step over---");
            }

        }

        System.out.printf("End positions head: %s and %s%n", head.x, head.y);
        System.out.printf("End positions tail: %s and %s%n", tail.x, tail.y);

    }

    private static boolean adjacentPositions(Position head, Position tail) {
        // if larger than 1, they are no longer adjacent
        return !(Math.abs(head.x - tail.x) > 1) && !(Math.abs(head.y - tail.y) > 1);
    }


}

class Position {

    int x = 0;
    int y = 0;

    public Position() {
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(Position newPosition) {
        this.x = newPosition.x;
        this.y = newPosition.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

