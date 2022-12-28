import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] gas = parseInput("input-test.txt");

        // Puzzle 1
        int height = dropRocks(2022, gas);
        System.out.printf("Puzzle 1 height: %s%n", height);

        // Puzzle 2
        // Least common multiple, na dit getal begint het geheel zich te herhalen

        int lcm = Rockshape.values().length * gas.length;
        long count = 1_000_000_000_000L;
        long lcm_fit = count / lcm;
        int lcm_remainder = (int)(count % lcm);

        long height_p2_lcm = dropRocks(lcm, gas);
        long height_p2_lcm_remainder = dropRocks(lcm_remainder, gas);

        long result = (height_p2_lcm * lcm_fit) + height_p2_lcm_remainder;
        System.out.printf("Puzzle 2 height: %s%n", result);


    }

    private static int dropRocks(final int drops, final String[] gas) {

        byte[] field = new byte[drops * 5];

        int move = 0;
        int dropped = 0;
        Rock rock = new Rock(Rockshape.values()[dropped % Rockshape.values().length], getTopRow(field) + 3);
//        printField(field, rock);

        // While dropCount < drops
        // Correct answer = 3068
//        while (move < 100) {
        while (dropped < drops) {

            // Create new rock when previous is done falling
            if (!rock.isFalling) {
                rock = new Rock(Rockshape.values()[dropped % Rockshape.values().length], getTopRow(field) + 4);
//                printField(field, rock);
            }

            if (canApplyJet(rock, gas[move % gas.length], field)) {
                applyJet(rock, gas[move % gas.length]);
            }
            move++;

            // If dropped down, means going into the floor/rock, stop moving
            if (canDrop(field, rock)) {
                rock.fall();
            } else {
                rock.isFalling = false;
                dropped++;
                pushRockOnField(field, rock);
            }
//            printField(field, rock);
        }

        System.out.printf("Dropped %s rocks, tower height is %s rows%n", dropped, getTopRow(field) + 1);

        return getTopRow(field) + 1;
    }

    private static void applyJet(Rock rock, String gas) {
        boolean left = gas.equals("<");
        for (int i = 0; i < rock.height; i++) {
            if (left) {
                rock.pattern[i] = (byte) (rock.pattern[i] << 1);
            } else {
                rock.pattern[i] = (byte) (rock.pattern[i] >> 1);
            }
        }
    }

    private static boolean canApplyJet(Rock rock, String gas, byte[] field) {
        // Check walls & field
        for (int i = rock.getBottomY(); i <= rock.getTopY(); i++) {
            // If any part of the rock is touching left wall
            if (gas.equals("<")) {
//                System.out.print("Trying to move left .. ");

                if ((rock.patternAtY(i) & 0b01000000) == 0b01000000) {
//                    System.out.println("blocked by wall");
                    return false;
                }
                // Check if rock would be touching any other rocks on the field
                byte moved = (byte) (rock.patternAtY(i) << 1);
                if ((moved & field[i]) != 0) {
//                    System.out.println("blocked by other piece");
                    return false;
                }
            }
            // or touching right wall
            else {
//                System.out.print("Trying to move right .. ");

                if ((rock.patternAtY(i) & 0b00000001) == 0b00000001) {
//                    System.out.println("blocked by wall");
                    return false;
                }
                // Check if rock would be touching any other rocks on the field
                byte moved = (byte) (rock.patternAtY(i) >> 1);
                if ((moved & field[i]) != 0) {
//                    System.out.println("blocked by other piece");
                    return false;
                }
            }
        }
//        System.out.println("succeeded!");
        return true;
    }

    private static void pushRockOnField(byte[] field, Rock rock) {
        for (int i = rock.getBottomY(); i <= rock.getTopY(); i++) {
            field[i] |= rock.patternAtY(i);
        }
    }

    private static boolean canDrop(byte[] field, Rock rock) {
        // We are at the bottom
        if (rock.y - 1 < 0) {
            return false;
        }

        for (int i = rock.getBottomY(); i <= rock.getTopY(); i++) {
            if ((field[i - 1] & rock.patternAtY(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    private static void printField(byte[] field, Rock rock) {

        int y = rock.y + rock.height - 1;
        int towerY = getTopRow(field);
        int maxY = Math.max(y, towerY);

        for (int i = maxY; i >= 0; i--) {
            if (rock.isFalling && i <= rock.getTopY() && i >= rock.getBottomY()) {
                System.out.println(rockToString(rock.patternAtY(i), true));
            } else if (i <= towerY) {
                System.out.println(rockToString(field[i], false));
            } else {
                System.out.println("|.......|");
            }
        }
        System.out.println("+-------+");
        System.out.println("");
    }

    private static String rockToString(byte shape, boolean isRock) {
        String indicator = isRock ? "@" : "#";
        String s = String.format("%8s|", Integer.toBinaryString(shape & 0xFF)).replace(' ', '0');
        return "|" + s.substring(1).replace("0", ".").replace("1", indicator);
    }

    private static int getTopRow(byte[] field) {
        for (int i = field.length - 1; i >= 0; i--) {
            if (field[i] != 0) {
                return i;
            }
        }
        return 0;
    }

    private static String[] parseInput(String s) throws IOException {
        return Files.readAllLines(Paths.get("day-17/" + s)).get(0).split("");
    }
}

class Rock {
    final Rockshape rockshape;
    final byte[] pattern;
    final int height;
    boolean isFalling = true;
    int y = 0;

    public Rock(Rockshape rockshape, int y) {
        this.rockshape = rockshape;
        this.pattern = rockshape.shape.clone();
        this.y = y;
        this.height = rockshape.shape.length;
    }

    public void fall() {
        this.y--;
    }

    public byte patternAtY(int y) {
        int row = this.y - y + this.height - 1;
        return pattern[row];
    }

    public int getBottomY() {
        return this.y;
    }

    public int getTopY() {
        return this.y + this.height - 1;
    }
}

enum Rockshape {

    MINUS((byte) 0b00011110),
    PLUS((byte) 0b00001000,
            (byte) 0b00011100,
            (byte) 0b00001000),
    L((byte) 0b00000100,
            (byte) 0b00000100,
            (byte) 0b00011100),
    I((byte) 0b00010000,
            (byte) 0b00010000,
            (byte) 0b00010000,
            (byte) 0b00010000),
    SQUARE((byte) 0b00011000,
            (byte) 0b00011000);

    final byte[] shape;

    Rockshape(byte... shape) {
        this.shape = shape;
    }
}