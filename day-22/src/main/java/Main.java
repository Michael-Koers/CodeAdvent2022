import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        String file = "input.txt";
        Password p = parseInput(file);

        long password = 0;

        try{
            password = findPassword(p);
            printField(p.field);
        } catch (Exception e) {
            printField(p.field);
            System.out.println(e);
        }

        System.out.printf("Puzzle 1 - Final password: %s%n", password);
    }

    private static int findPassword(Password p) {

        Position pos = new Position(Direction.RIGHT, p.getFirstLeftPosition(0), 0);
        p.position = pos;

        for (String step : p.steps) {

            if (step.contains("L")) {
                pos.direction = pos.direction.turnLeft();
            } else if (step.contains("R")) {
                pos.direction = pos.direction.turnRight();
            } else {
                int numberOfSteps = Integer.parseInt(step);

                for (int i = 0; i < numberOfSteps; i++) {
                    move(pos, p);
                }
            }
        }

        printEndToField(pos.xPosition, pos.yPosition, p.field);
        return ((pos.yPosition + 1) * 1000) + ((pos.xPosition+1) * 4) + pos.direction.value;
    }

    private static void printEndToField(Integer xPosition, Integer yPosition, String[][] field) {
        field[yPosition][xPosition] = "X";
    }

    private static void move(Position pos, Password p) {

        printDirectionToField(pos.xPosition, pos.yPosition, pos.direction, p.field);

        int newX = pos.xPosition;
        int newY = pos.yPosition;

        switch (pos.direction) {
            case RIGHT -> newX = determineNewX(pos.xPosition + 1, pos.yPosition, p);
            case DOWN -> newY = determineNewY(pos.xPosition, pos.yPosition + 1, p);
            case LEFT -> newX = determineNewX(pos.xPosition - 1, pos.yPosition, p);
            case UP -> newY = determineNewY(pos.xPosition, pos.yPosition - 1, p);
        }

        if (isTileFree(newX, newY, p)) {
            pos.xPosition = newX;
            pos.yPosition = newY;
        }

    }

    private static boolean isTileFree(int x, int y, Password p) {
        return !p.field[y][x].equals("#") && !p.field[y][x].isBlank();
    }

    private static int determineNewY(int x, int y, Password p) {
        if (y < 0) {
            y = p.getFirstBottomPosition(x);
        } else if (y > p.getFirstBottomPosition(x)) {
            y = p.getFirstTopPosition(x);
        }

        return y;
    }

    private static int determineNewX(int x, int y, Password p) {
        // Check out of bounds
        if (x < 0) {
            x = p.getFirstRightPosition(y);
        } else if (x > p.getFirstRightPosition(y)) {
            x = p.getFirstLeftPosition(y);
        }

        return x;
    }

    private static void printDirectionToField(Integer x, Integer y, Direction direction, String[][] field) {
        String symbol = switch (direction) {
            case RIGHT -> ">";
            case DOWN -> "v";
            case LEFT -> "<";
            case UP -> "^";
        };
        field[y][x] = symbol;
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
        String[][] field = new String[lines.size()-2][maxLineLength];

        int index = 0;
        // Read map
        while (!lines.get(index).isBlank()) {
            field[index] = lines.get(index).split("");
            index++;
        }

        // Last line of input are the steps
        steps.addAll(List.of(lines.get(lines.size() - 1).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")));

        System.out.println(steps.stream().collect(Collectors.joining()));
        return new Password(steps, field);
    }


}


class Password {

    final List<String> steps;
    final String[][] field;
    Position position;

    Password(List<String> steps, String[][] field) {
        this.steps = steps;
        this.field = field;
    }

    public int getFirstLeftPosition(int y) {
        String[] line = this.field[y];
        for (int i = 0; i < line.length; i++) {
            if (!line[i].isBlank()) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstRightPosition(int y) {
        String[] line = this.field[y];
        for (int i = line.length - 1; i >= 0; i--) {
            if (!line[i].isBlank()) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstTopPosition(int x) {
        for (int i = 0; i < field.length; i++) {
            if (field[i][x] != null && !field[i][x].isBlank()) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstBottomPosition(int x) {
        for (int i = field.length - 1; i >= 0; i--) {
            if (field[i].length > x && field[i][x] != null && !field[i][x].isBlank()) {
                return i;
            }
        }
        return -1;
    }
}

class Position {
    Direction direction;
    Integer xPosition;
    Integer yPosition;

    public Position(Direction direction, Integer xPosition, Integer yPosition) {
        this.direction = direction;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}

enum Direction {
    LEFT(2),
    DOWN(1),
    RIGHT(0),
    UP(3);

    int value;

    Direction(int value) {
        this.value = value;
    }

    public Direction turnLeft() {
        return findByValue(Math.floorMod(this.value - 1, 4));
    }

    public Direction turnRight() {
        return  findByValue(Math.floorMod(this.value + 1, 4));
    }

    public Direction findByValue(int number) {
        for (Direction d : values()) {
            if (d.value == number) {
                return d;
            }
        }
        return null;
    }
}