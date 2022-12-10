import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class main {

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("input.txt"));

        Set<Integer> positionsVisited = new HashSet<>();

        Position head = new Position();
        List<Position> knots = List.of(
                head,
                new Position(),
                new Position(),
                new Position(),
                new Position(),
                new Position(),
                new Position(),
                new Position(),
                new Position(),
                new Position()
        );

        // Include starting position
        positionsVisited.add(knots.get(0).toString().hashCode());

        for (String line : lines) {

            String[] cmd = line.split(" ");
            int steps = Integer.parseInt(cmd[1]);

            for (int i = 0; i < steps; i++) {

                switch (cmd[0]) {
                    case "D" -> --head.y;
                    case "U" -> ++head.y;
                    case "L" -> --head.x;
                    case "R" -> ++head.x;
                }

                for (int j = 1; j < knots.size(); j++) {

                    if (!adjacentPositions(knots.get(j - 1), knots.get(j))) {
                        knots.get(j).follow(knots.get(j - 1));

                        // Final knot
                        if (j == knots.size() - 1) {
                            positionsVisited.add(knots.get(j).toString().hashCode());
                        }
                    }
                }
            }
        }

        System.out.printf("Total positions covered: %s", positionsVisited.size());
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

    public void follow(Position head) {
        // The use of Signum was stolen from the internet :)
        this.x += Math.signum(head.x - this.x);
        this.y += Math.signum(head.y - this.y);
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

