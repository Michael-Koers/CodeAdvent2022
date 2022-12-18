import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {

        String file = "input.txt";
        Tile[][] cave = parseInput(file, 400);

//        fillCave_part1(cave);
        fillCave_part2(cave);
    }

    private static void fillCave_part1(Tile[][] cave) {

        int spawnY = 0;
        int spawnX = 0;

        int index = 0;

        // Find spawn X coord
        while (spawnX == 0 && index < cave[0].length) {
            if (cave[0][index] == Tile.SPAWN) {
                spawnX = index;
            }
            index++;
        }

        // Start filling cave
        int amountDropped = 0;

        try {
            while (true) {
                Sandbag b = new Sandbag(spawnX, spawnY);
                dropSandbag(cave, b);
                amountDropped++;
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Sandbags overflowing!");
            printCave(cave);
            System.out.printf("Number of sandbags dropped: %s", amountDropped);
        }
    }

    private static void fillCave_part2(Tile[][] cave) {

        // Create an extended cave with bedrock
        Tile[][] extendedCave = Arrays.copyOf(cave, cave.length + 2);

        // Initialize the new rows
        extendedCave[extendedCave.length - 2] = new Tile[extendedCave[0].length];
        extendedCave[extendedCave.length - 1] = new Tile[extendedCave[0].length];

        // Fill 2nd to last row
        Arrays.fill(extendedCave[extendedCave.length - 2], Tile.AIR);
        // Fill last row
        Arrays.fill(extendedCave[extendedCave.length - 1], Tile.ROCK);

//        printCave(extendedCave);

        int spawnY = 0;
        int spawnX = 0;

        int index = 0;

        // Find spawn X coord
        while (spawnX == 0 && index < cave[0].length) {
            if (extendedCave[0][index] == Tile.SPAWN) {
                spawnX = index;
            }
            index++;
        }

        // Start filling cave
        int amountDropped = 0;

        boolean run = true;

        while (run) {
            Sandbag b = new Sandbag(spawnX, spawnY);
            dropSandbag(extendedCave, b);
            amountDropped++;

            if (!b.isFalling()
                    && b.getX() == spawnX
                    && b.getY() == spawnY) {
                run = false;
                System.out.println("Sandbags reached the top!");
                printCave(extendedCave);
                System.out.printf("Number of sandbags dropped: %s", amountDropped);
            }
        }
    }

    private static void dropSandbag(Tile[][] cave, Sandbag b) {
        while (b.isFalling()) {
            int currX = b.getX();
            int currY = b.getY();

            // Check below
            if (cave[currY + 1][currX] == Tile.AIR) {
                b.setY(b.getY() + 1);
            }
            // Check below left
            else if (cave[currY + 1][currX - 1] == Tile.AIR) {
                b.setY(b.getY() + 1);
                b.setX(b.getX() - 1);
            }
            // Check below right
            else if (cave[currY + 1][currX + 1] == Tile.AIR) {
                b.setY(b.getY() + 1);
                b.setX(b.getX() + 1);
            }
            // Nowhere to go
            else {
                cave[currY][currX] = Tile.SAND;
                b.setFalling(false);

            }

//            previewSandbagInCave(cave, b.getX(), b.getY());
        }
    }

    private static void previewSandbagInCave(Tile[][] cave, int sX, int sY) {
        for (int y = 0; y < cave.length; y++) {
            for (int x = 0; x < cave[y].length; x++) {
                if (x == sX && y == sY) {
                    System.out.print(Tile.SAND.symbol);
                } else {
                    System.out.print(cave[y][x].symbol);
                }
            }
            System.out.printf("%n");
        }
    }

    private static void printCave(Tile[][] cave) {
        for (int y = 0; y < cave.length; y++) {
            for (int x = 0; x < cave[y].length; x++) {
                System.out.print(cave[y][x].symbol);
            }
            System.out.printf("%n");
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static Tile[][] parseInput(String file, int extraWidth) throws Exception {
        // Please ignore this mess

        List<String> lines = Files.readAllLines(Paths.get(file));
        Set<String> rocks = new HashSet<>();

        int lowestX = Integer.MAX_VALUE;
        int highestX = Integer.MIN_VALUE;
        int lowestY = Integer.MIN_VALUE;

        // Populate rocks set with all intermediate rocks
        for (String line : lines) {

            int prevX = -1;
            int prevY = -1;

            for (String coord : line.split(" -> ")) {

                int x = Integer.parseInt(coord.split(",")[0]) + extraWidth;
                int y = Integer.parseInt(coord.split(",")[1]);

                rocks.add(x + "," + y);

                // Record highest/lowest locations to reduce 2d array size
                if (x > highestX) {
                    highestX = x;
                }
                if (x < lowestX) {
                    lowestX = x;
                }
                if (y > lowestY) {
                    lowestY = y;
                }

                // Fill up rocks in between 2 points
                if (prevX >= 0) {

                    int tmpX = x;
                    int tmpY = y;

                    // PrevX ligt recht van huidige x
                    while (prevX > tmpX) {
                        tmpX++;
                        rocks.add(tmpX + "," + y);
                    }
                    // PrevX ligt links van huidige x
                    while (prevX < tmpX) {
                        tmpX--;
                        rocks.add(tmpX + "," + y);
                    }
                    // PrevY ligt boven huidige y
                    while (prevY < tmpY) {
                        tmpY--;
                        rocks.add(x + "," + tmpY);
                    }
                    // PrevY ligt onder huidige y
                    while (prevY > tmpY) {
                        tmpY++;
                        rocks.add(x + "," + tmpY);
                    }
                }
                prevX = x;
                prevY = y;
            }
        }

        lowestX -= extraWidth;

        Tile[][] cave = new Tile[lowestY + 1][highestX - lowestX + 1 + extraWidth];

        for (int y = 0; y < cave.length; y++) {
            for (int x = 0; x < cave[y].length; x++) {
                if (rocks.contains((lowestX + x) + "," + y)) {
                    cave[y][x] = Tile.ROCK;
                } else {
                    cave[y][x] = Tile.AIR;
                }
            }
        }

        cave[0][500 - lowestX + extraWidth] = Tile.SPAWN;

        return cave;

    }
}

enum Tile {
    AIR("."), SAND("o"), ROCK("#"), SPAWN("+");

    public final String symbol;

    Tile(String symbol) {
        this.symbol = symbol;
    }
}

class Sandbag {

    private boolean isFalling = true;
    private int x = 0;
    private int y = 0;

    public Sandbag(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public void setFalling(boolean falling) {
        isFalling = falling;
    }
}