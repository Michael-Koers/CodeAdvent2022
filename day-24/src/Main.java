import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        puzzle1();

    }

    record Point(int row, int col) {
        Point move(Point direction) {
            return new Point(row + direction.row, col + direction.col);
        }
    }

    static Map<Character, Point> directions = Map.of(
            '>', new Point(0, 1),
            '<', new Point(0, -1),
            '^', new Point(-1, 0),
            'v', new Point(1, 0)
    );

    static List<Point> blizzardPositions = new ArrayList<>();
    static List<Point> blizzardDirections = new ArrayList<>();

    static int mapLength = 0;
    static int mapHeight = 0;

    static Point start = new Point(0, 1);
    static Point end;
    static Point player = new Point(0, 1);

    public static void puzzle1() throws Exception {

        parseInput("input.txt");

        // BFS - Step 1: start to finish
        Set<Point> paths = new HashSet<>();

        paths.add(start);
        int minutes = 0;
//        printField();

        // Keep searching as long as we haven't reached the end
        while (!paths.contains(end)) {

            // Update blizzards
            updateBlizzardPositions();

            // Update player
            paths = updatePlayerPosition(paths, end);

            minutes++;
            //printField();
        }

        System.out.printf("Reached the end in %s minutes!%n", minutes);
        System.out.println("Oops, elf forgot his snacks... Time to get back to start!");

        // BFS - Step 2: finish to start
        paths.clear();
        paths.add(end);

        // Keep searching as long as we haven't reached the start again
        while (!paths.contains(start)) {
            // Update blizzards
            updateBlizzardPositions();
            // Update player
            paths = updatePlayerPosition(paths, start);
            minutes++;
        }

        System.out.printf("Reached the start again in %s minutes!%n", minutes);
        System.out.println("Time to head back to end, AGAIN!");

        // BFS - Step 3: start to finish, AGAIN!
        paths.clear();
        paths.add(start);

        // Keep searching as long as we haven't reached the start again
        while (!paths.contains(end)) {
            // Update blizzards
            updateBlizzardPositions();
            // Update player
            paths = updatePlayerPosition(paths, end);
            minutes++;
        }

        System.out.printf("Reached the end, with snacks(!), in %s minutes!%n", minutes);
    }

    private static Set<Point> updatePlayerPosition(Set<Point> paths, Point target) {

        Set<Point> possiblePaths = new HashSet<>();

        for (Point point : paths) {

            // Wait if next iteration of blizzards don't move to this field
            if (!blizzardPositions.contains(point)) {
                possiblePaths.add(point);
            }

            // Move, check all possible locations
            for (Point direction : directions.values()) {

                Point possibleLocation = point.move(direction);

                // If we found the target (end/start
                if (target.equals(possibleLocation)) {
                    possiblePaths.add(target);
                }

                // Can't step on borders
                if (possibleLocation.row <= 0 ||
                        possibleLocation.row >= mapHeight - 1 ||
                        possibleLocation.col <= 0 ||
                        possibleLocation.col >= mapLength - 1) {
                    continue;
                }

                if (!blizzardPositions.contains(possibleLocation)) {
                    possiblePaths.add(possibleLocation);
                }
            }
        }
        return possiblePaths;
    }


    private static void printField() {
        for (int r = 0; r < mapHeight; r++) {
            for (int c = 0; c < mapLength; c++) {
                // Print player
                if (r == player.row && c == player.col) {
                    System.out.print("E");
                }
                // Print start
                else if (r == 0 && c == 1) {
                    System.out.print(".");
                }
                // Print end
                else if (r == mapHeight - 1 && c == mapLength - 2) {
                    System.out.print(".");
                }
                // Print walls
                else if (r == 0 || r == mapHeight - 1 || c == 0 || c == mapLength - 1) {
                    System.out.print("#");
                }
                // Print blizzards
                else {
                    final int row = r;
                    final int col = c;

                    List<Point> blizzards = blizzardPositions.stream()
                            .filter(b -> b.row == row && b.col == col)
                            .collect(Collectors.toList());

                    if (blizzards.isEmpty()) {
                        System.out.print(".");
                    } else if (blizzards.size() == 1) {
                        int indexDir = blizzardPositions.indexOf(blizzards.get(0));
                        Point blizzardDirection = blizzardDirections.get(indexDir);
                        Character character = directions.entrySet()
                                .stream()
                                .filter(e -> e.getValue().equals(blizzardDirection))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow();
                        System.out.print(character);
                    } else {
                        System.out.print(blizzards.size());
                    }
                }
            }
            System.out.printf("%n");
        }
    }

    private static void updateBlizzardPositions() {

        for (int b = 0; b < blizzardPositions.size(); b++) {
            Point blizzard = blizzardPositions.get(b);
            Point direction = blizzardDirections.get(b);

            Point nextLocation = blizzard.move(direction);

            blizzardPositions.set(b, new Point(Math.floorMod(nextLocation.row - 1, mapHeight - 2) + 1,
                    Math.floorMod(nextLocation.col - 1, mapLength - 2) + 1));
        }
    }

    private static void parseInput(String file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("day-24/" + file));
        for (int r = 1; r < lines.size() - 1; r++) {
            String row = lines.get(r);

            for (int c = 1; c < row.length() - 1; c++) {
                // Search for blizzards
                if (row.charAt(c) != '.') {
                    blizzardPositions.add(new Point(r, c));
                    blizzardDirections.add(directions.get(row.charAt(c)));
                }
            }
        }
        end = new Point(lines.size() - 1, lines.get(0).length() - 2);
        mapLength = lines.get(0).length();
        mapHeight = lines.size();
    }
}




