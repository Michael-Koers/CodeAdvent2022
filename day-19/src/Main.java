import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    static int[] cost;

    static int maxTime;

    static int mostGeodes;

    public static void main(String[] args) throws Exception {

        String file = "day-19/input.txt";

//        puzzle1(file);
        puzzle2(file);

    }

    private static void puzzle1(String file) throws Exception {
        int quality = 0;

        for (String line : Files.readAllLines(Paths.get(file))) {
            cost = new int[6];

            String[] bots = line.split(": |\\. ");

            // Blueprint number
            int blueprint = Integer.parseInt(bots[0].split(" ")[1]);

            //ore bot, ore
            cost[0] = Integer.parseInt(bots[1].split(" ")[4]);

            //clay bot, ore
            cost[1] = Integer.parseInt(bots[2].split(" ")[4]);

            //obsidian bot, ore + clay
            cost[2] = Integer.parseInt(bots[3].split(" ")[4]);
            cost[3] = Integer.parseInt(bots[3].split(" ")[7]);

            //geode bot, ore + obsidian
            cost[4] = Integer.parseInt(bots[4].split(" ")[4]);
            cost[5] = Integer.parseInt(bots[4].split(" ")[7]);

            maxTime = 24;
            mostGeodes = 0;

            int best = calculateMostGeodes(0, 1, 0, 0, 0, 0, 0, 0, 0);

            quality += blueprint * best;
        }
        System.out.printf("Puzzle 1 - Total quality level: %s%n", quality);
    }

    private static void puzzle2(String file) throws Exception {
        int quality = 1;

        List<String> lines = Files.readAllLines(Paths.get(file));

        // To prevent out-of-bounds for test input
        int size = Math.min(lines.size(), 3);

        for (int i = 0; i < size; i++) {
            cost = new int[6];

            String[] bots = lines.get(i).split(": |\\. ");

            // Blueprint number
            int blueprint = Integer.parseInt(bots[0].split(" ")[1]);

            //ore bot, ore
            cost[0] = Integer.parseInt(bots[1].split(" ")[4]);

            //clay bot, ore
            cost[1] = Integer.parseInt(bots[2].split(" ")[4]);

            //obsidian bot, ore + clay
            cost[2] = Integer.parseInt(bots[3].split(" ")[4]);
            cost[3] = Integer.parseInt(bots[3].split(" ")[7]);

            //geode bot, ore + obsidian
            cost[4] = Integer.parseInt(bots[4].split(" ")[4]);
            cost[5] = Integer.parseInt(bots[4].split(" ")[7]);

            maxTime = 32;
            mostGeodes = 0;

            int best = calculateMostGeodes(0, 1, 0, 0, 0, 0, 0, 0, 0);
            System.out.printf("Blueprint %s, geodes: %s%n", blueprint, best);
            quality *= best;
        }
        System.out.printf("Puzzle 2 - Total quality level: %s%n", quality);
    }

    private static int calculateMostGeodes(int ore, int oreRobots, int clay, int clayRobots, int obsidian, int obsidianRobots, int geodes, int geodeRobots, int time) {

        // We've reached end of a branch, save geode count if it is higher can current highest count
        if (time == maxTime) {
            mostGeodes = Math.max(mostGeodes, geodes);
            return geodes;
        }

        // Kill of branches that, with the time remaining, won't be able to beat the high score
        // Since all branches use the same logic, there is no way for branches that are behind to catch up
        int minsLeft = maxTime - time;
        int maxGeodesPossible = geodes;
        for (int i = 0; i < minsLeft; i++) {
            maxGeodesPossible += geodeRobots + i;
        }
        if (maxGeodesPossible < mostGeodes) {
            return 0;
        }

        // Collect ores
        int newOre = ore + oreRobots;
        int newClay = clay + clayRobots;
        int newObsidian = obsidian + obsidianRobots;
        int newGeodes = geodes + geodeRobots;
        // End collecting ores

        // Determine the max amount of bots we need for each resource
        int maxOreRobots = Math.max(Math.max(cost[0], cost[1]), Math.max(cost[2], cost[4]));
        int maxClayRobots = cost[3];
        int maxObsidianRobots = cost[5];

        int best = 0;

        // Make geode robot, this is always the best option, so no need to branch further from this branch
        if (ore >= cost[4] && obsidian >= cost[5]) {
            return calculateMostGeodes(newOre - cost[4], oreRobots, newClay, clayRobots, newObsidian - cost[5], obsidianRobots, newGeodes, geodeRobots + 1, time + 1);
        }
        // Make obsidian robot, if we have enough clay robots, no need to branch further from this branch
        if (clayRobots >= maxClayRobots && ore >= cost[2] && clay >= cost[3] && obsidianRobots < maxObsidianRobots) {
            return calculateMostGeodes(newOre - cost[2], oreRobots, newClay - cost[3], clayRobots, newObsidian, obsidianRobots + 1, newGeodes, geodeRobots, time + 1);
        }

        // Make obsidian robot
        if (ore >= cost[2] && clay >= cost[3] && obsidianRobots < maxObsidianRobots) {
            best = Math.max(best, calculateMostGeodes(newOre - cost[2], oreRobots, newClay - cost[3], clayRobots, newObsidian, obsidianRobots + 1, newGeodes, geodeRobots, time + 1));
        }
        // Make clay robot
        if (ore >= cost[1] && clayRobots < maxClayRobots) {
            best = Math.max(best, calculateMostGeodes(newOre - cost[1], oreRobots, newClay, clayRobots + 1, newObsidian, obsidianRobots, newGeodes, geodeRobots, time + 1));
        }
        // Make ore robot
        if (ore >= cost[0] && oreRobots < maxOreRobots) {
            best = Math.max(best, calculateMostGeodes(newOre - cost[0], oreRobots + 1, newClay, clayRobots, newObsidian, obsidianRobots, newGeodes, geodeRobots, time + 1));

        }
        // Do nothing
        if (ore <= maxOreRobots) {
            best = Math.max(best, calculateMostGeodes(newOre, oreRobots, newClay, clayRobots, newObsidian, obsidianRobots, newGeodes, geodeRobots, time + 1));
        }

        return best;
    }
}

