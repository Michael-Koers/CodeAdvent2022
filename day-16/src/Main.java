import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        Map<String, Valve> valves = parseInput("input.txt");

        Map<Route, Integer> costs = calculateCosts(valves);

        // Puzzle 1
//        State best = openValves(valves, costs);

        // Puzzle 2
        long startTime = System.nanoTime();
        StateP2 bestWithElephant = openValvesWithElephant(valves, costs);

        System.out.printf("Duration: %sms%n", (System.nanoTime() - startTime) / 1_000_000);
//        System.out.printf("Best path leads to %s pressure released%n", best.pressure());
        System.out.printf("Best path with elephant leads to %s pressure released%n", bestWithElephant.pressure());
    }

    private static StateP2 openValvesWithElephant(Map<String, Valve> valves, Map<Route, Integer> costs) {

        Player human = new Player("AA", 26);
        Player elephant = new Player("AA", 26);

        Set<StateP2> cache = new HashSet<>();

        LinkedList<StateP2> attempts = new LinkedList<>();
        attempts.add(new StateP2(human, elephant, 0, new ArrayList<>()));

        int cacheHitCounter = 0;
        StateP2 best = null;

        while (!attempts.isEmpty()) {
            StateP2 attempt = attempts.poll();

            if (cache.contains(attempt)) {
                cacheHitCounter++;
                if (cacheHitCounter % 1000 == 0) {
                    int bestPressure = best == null ? 0 : best.pressure();
                    System.out.printf("Cache hits: %s, attempts remaning: %s, best attempt: %s%n", cacheHitCounter, attempts.size(), bestPressure);
                }
                continue;
            }
            // Determine who to move first
            boolean moveHuman = attempt.human().timeLeft() >= attempt.elephant().timeLeft();

            // If the one can't move anymore, the other still might
            boolean canMove = movePlayer(attempt, attempts, valves, costs, moveHuman);

            if(!canMove) {
                canMove = movePlayer(attempt, attempts, valves, costs, !moveHuman);
            }

            // If both can't move anymore, end attempt, check if result is higher than best known result
            if (!canMove) {
                if (best == null || attempt.pressure() > best.pressure()) {
                    best = attempt;
                }
            }
            cache.add(attempt);
        }

        return best;
    }

    private static boolean movePlayer(StateP2 attempt, List<StateP2> attempts, Map<String, Valve> valves, Map<Route, Integer> costs, boolean isHuman) {

        Player toMove = isHuman ? attempt.human() : attempt.elephant();

        List<StateP2> newStates = costs.entrySet().stream()
                .filter(e -> e.getKey().from().equals(toMove.current()))
                .filter(e -> !attempt.opened().contains(e.getKey().to()))
                .filter(e -> toMove.timeLeft() >= e.getValue() + 1)
                .map(e -> attempt.moveToAndOpen(toMove, e.getKey().to(), e.getValue(), valves.get(e.getKey().to()).flowRate, isHuman))
                .collect(Collectors.toList());

        // This helps with caching
        newStates.forEach(ns -> Collections.sort(ns.opened()));

        // Add new states to start of the list as to prevent OutOfMemory
        attempts.addAll(0, newStates);
        return !newStates.isEmpty();
    }

    private static State openValves(Map<String, Valve> valves, Map<Route, Integer> costs) {

        LinkedList<State> attempts = new LinkedList<>();
        attempts.add(new State("AA", 30, 0, new ArrayList<>()));

        State best = null;

        while (!attempts.isEmpty()) {
            State state = attempts.poll();

            List<State> newStates = costs.entrySet().stream()
                    // Filter to route from current valve
                    .filter(e -> e.getKey().from().equals(state.current()))
                    // We haven't visited the valve yet
                    .filter(e -> !state.opened().contains(e.getKey().to()))
                    // We can afford to go to the valve
                    .filter(e -> state.timeLeft() >= e.getValue() + 1)
                    // Create new states for each possibility
                    .map(e -> state.moveToAndOpen(e.getKey().to(), e.getValue(), valves.get(e.getKey().to()).flowRate))
                    .collect(Collectors.toList());

            if (newStates.isEmpty()) {
                if (best == null || state.pressure() > best.pressure()) {
                    best = state;
                }
            }
            attempts.addAll(newStates);
        }
        return best;
    }

    private static Map<Route, Integer> calculateCosts(Map<String, Valve> valves) {
        // Floyd Warshall this bitch
        Map<Route, Integer> costs = new HashMap<>();

        // Get all the basic distances in, the ones we already know about
        for (Valve valve : valves.values()) {
            valve.neighbour.forEach(target -> costs.put(new Route(valve.name, target), 1));
        }

        // Not exactly sure how the three for-loops work
        for (Valve via : valves.values()) {
            for (Valve source : valves.values()) {
                for (Valve dest : valves.values()) {
                    if (via == source || source == dest || dest == via) {
                        continue;
                    }

                    // Calcute distance via the intermediate node (valve)
                    Route part1 = new Route(source.name, via.name);
                    Route part2 = new Route(via.name, dest.name);

                    // If routes are known (if not, it is a non-existent edge)
                    if (costs.containsKey(part1) && costs.containsKey(part2)) {

                        Route directRoute = new Route(source.name, dest.name);

                        // Check whether this new found connection from source to destination is faster
                        // than either an existing connection or INFINITY.
                        Integer directCost = costs.get(part1) + costs.get(part2);
                        if (directCost < costs.getOrDefault(directRoute, Integer.MAX_VALUE)) {
                            costs.put(directRoute, directCost);
                        }
                    }
                }
            }
        }

        // Remove valves that are not interesting to visit as a source/destination because they don't provide any flowrate
        List<String> irrelevantValves = valves.values().stream()
                .filter(v -> v.flowRate == 0 && !v.name.equals("AA"))
                .map(v -> v.name)
                .toList();
        List<Route> irrelevantRoutes = costs.keySet().stream().filter(route -> irrelevantValves.contains(route.to()) || irrelevantValves.contains(route.from())).toList();
        irrelevantRoutes.forEach(costs::remove);

        return costs;
    }

    private static Map<String, Valve> parseInput(String s) throws IOException {

        Map<String, Valve> valves = new HashMap<>();

        for (String line : Files.readAllLines(Paths.get("day-16/" + s))) {

            String valveInfo = line.split(";")[0];
            List<String> neighbours = Arrays.stream(line.split(";")[1]
                    .replace("tunnels lead to valves ", "")
                    .replace("tunnel leads to valve ", "")
                    .trim()
                    .split(", "))
                    .toList();

            valves.put(valveInfo.split(" ")[1], new Valve(valveInfo.split(" ")[1], Integer.parseInt(valveInfo.split("=")[1]), neighbours));
        }
        return valves;
    }
}

record State(String current, int timeLeft, int pressure, List<String> opened) {

    State moveToAndOpen(String destination, int timeCost, int flowRate) {
        List<String> opened = new ArrayList<>(this.opened);
        opened.add(destination);

        int timeLeft = this.timeLeft - timeCost - 1;
        int pressure = this.pressure + (flowRate * timeLeft);

        return new State(destination, timeLeft, pressure, opened);
    }
}

record Player(String current, int timeLeft) {
};

record StateP2(Player human, Player elephant, int pressure, List<String> opened) {
    StateP2 moveToAndOpen(Player player, String destination, int timeCost, int flowRate, boolean isHuman) {
        List<String> opened = new ArrayList<>(this.opened);
        opened.add(destination);

        int timeLeft = player.timeLeft() - timeCost - 1;
        int pressure = this.pressure + (flowRate * (player.timeLeft() - timeCost - 1));

        Player human = this.human;
        Player elephant = this.elephant;

        if (isHuman) {
            human = new Player(destination, timeLeft);
        } else {
            elephant = new Player(destination, timeLeft);
        }

        return new StateP2(human, elephant, pressure, opened);
    }
};

record Route(String from, String to) {
}

class Valve {

    final String name;
    final int flowRate;
    final List<String> neighbour;

    Valve(String name, int flowRate, List<String> neighbour) {
        this.name = name;
        this.flowRate = flowRate;
        this.neighbour = neighbour;
    }
}