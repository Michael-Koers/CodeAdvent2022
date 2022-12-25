import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static boolean elfsNotMoved = false;

    public static void main(String[] args) throws Exception {

        long startTime = System.nanoTime();

        List<Elf> elves = parseInput("input-test.txt");

        // For debugging purposes
//        System.out.println("== Initial State ==");
//        printElves(elves);
//        System.out.println("");

        // For puzzle 1
        int rounds = 0;
//        while (rounds < 10) {

        // For puzzle 2
        while (!elfsNotMoved) {
            rounds++;

            // Determine new location
            Map<Elf, Point> newElfLocations = determineNewLocations(elves);

            // For puzzle 2, if no elf wanted to move, we found the answer for round 2
            if (newElfLocations.isEmpty()) {
                elfsNotMoved = true;
            }

            // Try to move the elfs
            moveElfs(newElfLocations);

            // Show their new locations, debugging purposes
//            System.out.printf("== End of round %s ==%n", rounds);
//            printElves(elves);
//            System.out.println("");
        }

        int count = countEmptyGroundTiles(elves);

        System.out.printf("Total empty ground tiles: %s%n", count);
        System.out.printf("First elf stopped moving in round %s (system duration: %sms)%n", rounds, (System.nanoTime() - startTime) / 1_000_000);
    }

    private static int countEmptyGroundTiles(List<Elf> elves) {
        int minX = elves.stream()
                .mapToInt(e -> e.position.col())
                .min()
                .orElseThrow();

        int maxX = elves.stream()
                .mapToInt(e -> e.position.col())
                .max()
                .orElseThrow();

        int minY = elves.stream()
                .mapToInt(e -> e.position.row())
                .min()
                .orElseThrow();

        int maxY = elves.stream()
                .mapToInt(e -> e.position.row())
                .max()
                .orElseThrow();

        // +1 to offset... something?
        int colSize = Math.abs(maxX - minX) + 1;
        int rowSize = Math.abs(maxY - minY) + 1;

        return (colSize * rowSize) - elves.size();
    }

    private static void printElves(List<Elf> elves) {
        int minX = elves.stream()
                .mapToInt(e -> e.position.col())
                .min()
                .orElseThrow();

        int maxX = elves.stream()
                .mapToInt(e -> e.position.col())
                .max()
                .orElseThrow();

        int minY = elves.stream()
                .mapToInt(e -> e.position.row())
                .min()
                .orElseThrow();

        int maxY = elves.stream()
                .mapToInt(e -> e.position.row())
                .max()
                .orElseThrow();

        // -1 and +1 for an extra border/layer around the field, looks a little cleaner
        for (int y = minY - 1; y <= maxY + 1; y++) {
            for (int x = minX - 1; x <= maxX + 1; x++) {
                final int row = y;
                final int col = x;

                Optional<Elf> elf = elves.stream()
                        .filter(e -> e.position.row() == row && e.position.col() == col)
                        .findFirst();

                if (elf.isPresent()) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println("");
        }
    }

    private static void moveElfs(Map<Elf, Point> newElfLocations) {

        for (Map.Entry<Elf, Point> elfLocation : newElfLocations.entrySet()) {

            Point nextLocation = elfLocation.getValue();
            Elf elf = elfLocation.getKey();

            boolean isUniqueMove = newElfLocations.entrySet().stream()
                    // Filter out current elf
                    .filter(e -> !e.getKey().equals(elfLocation.getKey()))
                    .map(Map.Entry::getValue)
                    .noneMatch(e -> e.equals(nextLocation));

            if (isUniqueMove) {
                elf.position = new Point(nextLocation.row(), nextLocation.col());
            }
        }
    }

    private static Map<Elf, Point> determineNewLocations(List<Elf> elves) {

        Map<Elf, Point> elfProposedLocations = new HashMap<>();
        List<Point> elfCurrentLocations = elves.stream().map(e -> e.position).collect(Collectors.toList());

        outer:
        for (Elf elf : elves) {

            // If no other elves around, don't move!
            if (isElfClear(elf, elfCurrentLocations)) {
                elf.moveFirstDirectionToEndOfQueue();
                continue;
            }

            for (Direction currentDirection : elf.directions) {
                switch (currentDirection) {
                    case NORTH -> {
                        if (arePositionsFree(elf, elfCurrentLocations, Direction.NORTH, Direction.NORTHWEST, Direction.NORTHEAST)) {
                            Point proposedLocation = elf.position.move(currentDirection.direction);
                            elfProposedLocations.put(elf, proposedLocation);
                            elf.moveFirstDirectionToEndOfQueue();
                            continue outer;
                        }
                    }
                    case SOUTH -> {
                        if (arePositionsFree(elf, elfCurrentLocations, currentDirection, Direction.SOUTHEAST, Direction.SOUTHWEST)) {
                            Point proposedLocation = elf.position.move(currentDirection.direction);
                            elfProposedLocations.put(elf, proposedLocation);
                            elf.moveFirstDirectionToEndOfQueue();
                            continue outer;
                        }
                    }
                    case WEST -> {
                        if (arePositionsFree(elf, elfCurrentLocations, currentDirection, Direction.NORTHWEST, Direction.SOUTHWEST)) {
                            Point proposedLocation = elf.position.move(currentDirection.direction);
                            elfProposedLocations.put(elf, proposedLocation);
                            elf.moveFirstDirectionToEndOfQueue();
                            continue outer;
                        }
                    }
                    case EAST -> {
                        if (arePositionsFree(elf, elfCurrentLocations, currentDirection, Direction.NORTHEAST, Direction.SOUTHEAST)) {
                            Point proposedLocation = elf.position.move(currentDirection.direction);
                            elfProposedLocations.put(elf, proposedLocation);
                            elf.moveFirstDirectionToEndOfQueue();
                            continue outer;
                        }
                    }
                }
            }
            // If the elf couldn't move anywhere
            elf.moveFirstDirectionToEndOfQueue();
        }
        return elfProposedLocations;
    }

    private static boolean isElfClear(Elf elf, List<Point> locations) {
        for (Direction direction : Direction.values()) {
            if (locations.contains(elf.position.move(direction.direction))) {
                return false;
            }
        }
        return true;
    }

    private static boolean arePositionsFree(Elf elf, List<Point> locations, Direction... directions) {
        for (Direction direction : directions) {
            if (!isPositionFree(elf, locations, direction)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPositionFree(Elf elf, List<Point> locations, Direction direction) {
        Point newLocation = elf.position.move(direction.direction);
        // Check if any other elf is at this new location
        return locations.stream().noneMatch(l -> l.equals(newLocation));
    }

    private static List<Elf> parseInput(String file) throws IOException {
        List<Elf> elves = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get("day-23/" + file));
        for (int r = 0; r < lines.size(); r++) {
            String row = lines.get(r);
            for (int c = 0; c < row.length(); c++) {
                if (row.charAt(c) == '#') {
                    elves.add(new Elf(new Point(r, c)));
                }
            }
        }
        return elves;
    }
}

class Elf {

    Queue<Direction> directions = new LinkedList<>();
    Point position;

    public Elf(Point position) {
        this.position = position;
        directions.add(Direction.NORTH);
        directions.add(Direction.SOUTH);
        directions.add(Direction.WEST);
        directions.add(Direction.EAST);
    }

    public void moveFirstDirectionToEndOfQueue() {
        // Puts first direction back at the end of the queue
        Direction dir = directions.remove();
        this.directions.add(dir);
    }
}

record Point(int row, int col) {

    Point move(Point point) {
        return new Point(this.row + point.row, this.col + point.col);
    }
}

enum Direction {
    NORTH("N", new Point(-1, 0)),
    NORTHEAST("NE", new Point(-1, 1)),
    NORTHWEST("NW", new Point(-1, -1)),
    SOUTH("S", new Point(1, 0)),
    SOUTHEAST("SE", new Point(1, 1)),
    SOUTHWEST("SW", new Point(1, -1)),
    EAST("E", new Point(0, 1)),
    WEST("W", new Point(0, -1));

    String symbol;
    Point direction;

    Direction(String symbol, Point direction) {
        this.symbol = symbol;
        this.direction = direction;
    }
}