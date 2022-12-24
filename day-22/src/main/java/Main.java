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

        try {
            password = findPassword(p);
            printField(p.field);
        } catch (Exception e) {
            printField(p.field);
            System.out.println(e);
        }

        System.out.printf("xPos: %s, yPos: %s, Direction: %s(%s)%n", p.position.xPosition, p.position.yPosition, p.position.direction, p.position   .direction.value);
        System.out.printf("Final password: %s%n", password);
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
        return ((pos.yPosition + 1) * 1000) + ((pos.xPosition + 1) * 4) + pos.direction.value;
    }

    private static void printEndToField(int xPosition, int yPosition, String[][] field) {
        field[yPosition][xPosition] = "X";
    }

    private static void move(Position pos, Password p) {

        printDirectionToField(pos.xPosition, pos.yPosition, pos.direction, p.field);

        Position newPosition = new Position(pos.direction, pos.xPosition, pos.yPosition);

        // For puzzle 1
//        switch (pos.direction) {
//            case RIGHT -> newX = determineNewX(pos.xPosition + 1, pos.yPosition, p);
//            case LEFT -> newX = determineNewX(pos.xPosition - 1, pos.yPosition, p);
//            case DOWN -> newY = determineNewY(pos.xPosition, pos.yPosition + 1, p);
//            case UP -> newY = determineNewY(pos.xPosition, pos.yPosition - 1, p);
//        }

        // For puzzle 2
        switch (pos.direction) {
            case RIGHT -> determineNewCubePosition(pos.xPosition + 1, pos.yPosition, p, newPosition);
            case LEFT -> determineNewCubePosition(pos.xPosition - 1, pos.yPosition, p, newPosition);
            case DOWN -> determineNewCubePosition(pos.xPosition, pos.yPosition + 1, p, newPosition);
            case UP -> determineNewCubePosition(pos.xPosition, pos.yPosition - 1, p, newPosition);
        }

        if (isTileFree(newPosition.xPosition, newPosition.yPosition, p)) {
            pos.xPosition = newPosition.xPosition;
            pos.yPosition = newPosition.yPosition;
            pos.direction = newPosition.direction;
        }
    }

    private static boolean isTileFree(int x, int y, Password p) {
        return !p.field[y][x].equals("#") && !p.field[y][x].isBlank();
    }


    private static int determineNewY(int x, int y, Password p) {
        // Check out of bounds
        if (y < 0 || y < p.getFirstTopPosition(x)) {
            y = p.getFirstBottomPosition(x);
        } else if (y > p.getFirstBottomPosition(x)) {
            y = p.getFirstTopPosition(x);
        }

        return y;
    }

    private static int determineNewX(int x, int y, Password p) {
        // Check out of bounds
        if (x < 0 || x < p.getFirstLeftPosition(y)) {
            x = p.getFirstRightPosition(y);
        } else if (x > p.getFirstRightPosition(y)) {
            x = p.getFirstLeftPosition(y);
        }

        return x;
    }

    private static void determineNewCubePosition(int x, int y, Password p, Position position) {
        // Translate to new cube part
        /*
                       +------+------+
                       |  g   |   e  |
                       |f     |     d|
                       |      |  b   |
                       +------+------+
                       |      |
                       |a    b|
                       |      |
                +------+------+
                |   a  |      |
                |f     |     d|
                |      |   c  |
                1------+------+
                |      |
                |g    c|
                |   e  |
                2------+
         */
        int xCubePart = (int) Math.floor(position.xPosition / 50);
        int yCubePart = (int) Math.floor(position.yPosition / 50);

        int xRemainder = position.xPosition % 50;
        int yRemainder = position.yPosition % 50;

        // Middle piece
        if (xCubePart == 1 && yCubePart == 1) {
            // Edge a
            if (position.direction == Direction.LEFT && x < p.getFirstLeftPosition(y)) {

                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = yRemainder;
                position.yPosition = 100;
                position.direction = Direction.DOWN;

                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // Edge b
            else if (position.direction == Direction.RIGHT && x > p.getFirstRightPosition(y)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 100 + yRemainder;
                position.yPosition = 49;
                position.direction = Direction.UP;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            } else {
                position.xPosition = x;
                position.yPosition = y;
            }
        }
        // Top middle piece
        else if (xCubePart == 1 && yCubePart == 0) {
            // Edge g
            if (position.direction == Direction.UP && y < 0) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 0;
                position.yPosition = 150 + xRemainder;
                position.direction = Direction.RIGHT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // Edge f
            else if (position.direction == Direction.LEFT && x < p.getFirstLeftPosition(y)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 0;
                position.yPosition = 149 - yRemainder;
                position.direction = Direction.RIGHT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            } else {
                position.xPosition = x;
                position.yPosition = y;
            }
        }
        // Top right piece
        else if (xCubePart == 2 && yCubePart == 0) {
            // Edge e
            if (position.direction == Direction.UP && y < 0) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.yPosition = 199;
                position.xPosition = xRemainder;
                position.direction = Direction.UP;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // Edge d
            else if (position.direction == Direction.RIGHT && x > p.getFirstRightPosition(y)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 99;
                position.yPosition = 149 - yRemainder;
                position.direction = Direction.LEFT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // Edge b
            else if (position.direction == Direction.DOWN && y > p.getFirstBottomPosition(x)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 99;
                position.yPosition = 50 + xRemainder;
                position.direction = Direction.LEFT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            } else {
                position.xPosition = x;
                position.yPosition = y;
            }
        }
        // Bottom middle piece
        else if (xCubePart == 1 && yCubePart == 2) {
            // edge d
            if (position.direction == Direction.RIGHT && x > p.getFirstRightPosition(y)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 149;
                position.yPosition = 49 - yRemainder;
                position.direction = Direction.LEFT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // edge c
            else if (position.direction == Direction.DOWN && y > p.getFirstBottomPosition(x)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 49;
                position.yPosition = 150 + xRemainder;
                position.direction = Direction.LEFT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            } else {
                position.xPosition = x;
                position.yPosition = y;
            }
        }
        // Bottem left piece
        else if (xCubePart == 0 && yCubePart == 2) {
            // edge a
            if (position.direction == Direction.UP && x >= 0 && y < p.getFirstTopPosition(x)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 50;
                position.yPosition = 50 + xRemainder;
                position.direction = Direction.RIGHT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // edge f
            else if (position.direction == Direction.LEFT && x < 0) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 50;
                position.yPosition = 49 - yRemainder;
                position.direction = Direction.RIGHT;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            } else {
                position.xPosition = x;
                position.yPosition = y;
            }
        }
        // bottem left most (corner) piece
        else if (xCubePart == 0 && yCubePart == 3) {
            // edge g
            if (position.direction == Direction.LEFT && x < 0) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 50 + yRemainder;
                position.yPosition = 0;
                position.direction = Direction.DOWN;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // edge e
            else if (position.direction == Direction.DOWN && y > p.getFirstBottomPosition(x)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 100 + xRemainder;
                position.yPosition = 0;
                position.direction = Direction.DOWN;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            }
            // edge c
            else if (position.direction == Direction.RIGHT && x > p.getFirstRightPosition(y)) {
                System.out.printf("%nTeleporting from x:%s, y:%s, d:%s(%s) --> ", position.xPosition, position.yPosition, position.direction, position.direction.value);
                position.xPosition = 50 + yRemainder;
                position.yPosition = 149;
                position.direction = Direction.UP;
                System.out.printf("x:%s, y:%s, d:%s(%s)%n", position.xPosition, position.yPosition, position.direction, position.direction.value);
            } else {
                position.xPosition = x;
                position.yPosition = y;
            }
        }
    }

    private static void printDirectionToField(int x, int y, Direction direction, String[][] field) {
        field[y][x] = switch (direction) {
            case RIGHT -> ">";
            case DOWN -> "v";
            case LEFT -> "<";
            case UP -> "^";
        };
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
        String[][] field = new String[lines.size() - 2][maxLineLength];

        int index = 0;
        // Read map
        while (!lines.get(index).isBlank()) {
            field[index] = lines.get(index).split("");
            index++;
        }

        // Last line of input are the steps
        steps.addAll(List.of(lines.get(lines.size() - 1).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")));

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
        return findByValue(Math.floorMod(this.value + 1, 4));
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