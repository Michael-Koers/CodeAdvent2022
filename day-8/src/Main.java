import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("input.txt"));

        var trees = to2dTreeField(lines);

        int result_part1 = 0;
        int result_part2 = 0;

        result_part1 += calculateOutsideTrees(trees);
        result_part1 += calculateInsideTrees(trees);

        result_part2 = findBestTree(trees);

        System.out.printf("Total visible: %s, highest view score %s%n", result_part1, result_part2);
    }

    private static int findBestTree(int[][] trees) {

        int highestScore = 0;

        for (int i = 1; i < trees.length - 1; i++) {
            for (int j = 1; j < trees[i].length - 1; j++) {
                int score = calculateScore(trees, i, j);
                if (score > highestScore) {
                    highestScore = score;
                }
            }
        }

        return highestScore;
    }

    private static int calculateScore(int[][] trees, int i, int j) {
        return calculateLeft(trees, i, j) * calculateRight(trees, i, j) * calculateTop(trees, i, j) * calculateBottom(trees, i, j);
    }

    private static int calculateBottom(int[][] trees, int x, int y) {
        int distance = 0;
        for (int i = x + 1; i < trees.length; i++) {
            distance++;
            if (trees[i][y] >= trees[x][y]) {
                break;
            }
        }
        return distance;
    }

    private static int calculateTop(int[][] trees, int x, int y) {
        int distance = 0;
        for (int i = x - 1; i >= 0; i--) {
            distance++;
            if (trees[i][y] >= trees[x][y]) {
                break;
            }
        }
        return distance;
    }

    private static int calculateRight(int[][] trees, int x, int y) {
        int distance = 0;
        for (int i = y + 1; i < trees[x].length; i++) {
            distance++;
            if (trees[x][i] >= trees[x][y]) {
                break;
            }
        }
        return distance;
    }

    private static int calculateLeft(int[][] trees, int x, int y) {
        int distance = 0;
        for (int i = y - 1; i >= 0; i--) {
            distance++;
            if (trees[x][i] >= trees[x][y]) {
                break;
            }
        }
        return distance;
    }

    public static int[][] to2dTreeField(List<String> trees) {
        int[][] tree = new int[trees.size()][trees.get(0).length()];

        for (int i = 0; i < trees.size(); i++)
            for (int j = 0; j < trees.get(i).length(); j++) {
                tree[i][j] = Integer.parseInt(trees.get(i).split("")[j]);
            }

        return tree;
    }

    public static int calculateOutsideTrees(int[][] trees) {
        // Length (left + right) + width (top + bottom)
        return (trees.length * 2) + (trees[0].length * 2) - 4;
    }

    public static int calculateInsideTrees(int[][] trees) {
        int visible = 0;

        for (int i = 1; i < trees.length - 1; i++) {
            for (int j = 1; j < trees[i].length - 1; j++) {
                if (isVisible(trees, i, j)) {
                    visible++;
                }
            }
        }

        return visible;
    }

    public static boolean isVisible(int[][] trees, int x, int y) {
        return checkLeft(trees, x, y) || checkRight(trees, x, y) || checkTop(trees, x, y) || checkBottom(trees, x, y);
    }

    public static boolean checkLeft(int[][] trees, int x, int y) {
        for (int i = y - 1; i >= 0; i--) {
            if (trees[x][i] >= trees[x][y]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkRight(int[][] trees, int x, int y) {
        for (int i = y + 1; i < trees[x].length; i++) {
            if (trees[x][i] >= trees[x][y]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkTop(int[][] trees, int x, int y) {
        for (int i = x - 1; i >= 0; i--) {
            if (trees[i][y] >= trees[x][y]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkBottom(int[][] trees, int x, int y) {
        for (int i = x + 1; i < trees.length; i++) {
            if (trees[i][y] >= trees[x][y]) {
                return false;
            }
        }
        return true;
    }
}

