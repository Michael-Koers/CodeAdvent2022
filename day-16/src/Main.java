import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        String file = "day-16/input-test.txt";

        List<Valve> valves = parseInput(file);

        Valve start = valves.stream().filter(v -> v.name.equals("AA")).findFirst().orElseThrow(NoSuchElementException::new);
        List<Valve> usefulValves = valves.stream().filter(v -> v.flowRate > 0 || v.name.equals("AA")).collect(Collectors.toList());

        List<Distance> distances = findDistances(usefulValves, valves);
        int pressure = findMostPressure(start, 30, new ArrayList<>(), usefulValves, distances);

        System.out.printf("Most released pressure: %s%n", pressure);
    }

    private static int findMostPressure(Valve start, int time, List<Valve> seenValves, List<Valve> usefulValves, List<Distance> distances) {

        seenValves.add(start);
        usefulValves.removeAll(seenValves);

        int bestFlow = 0;
        int timeLeft;

        for (String v : start.neighbours){
            if (usefulValves.stream().noneMatch(x -> x.name.equals(v))){
                continue;
            }

            Valve currV = getValve(usefulValves, v);

            Distance d = distances.stream()
                    .filter(dis -> dis.start.equals(start.name) && dis.end.equals(currV.name))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            timeLeft = time - d.distance - 1;

            if (timeLeft > 0){
                int flow = currV.flowRate * timeLeft;
                flow += findMostPressure(currV, timeLeft, seenValves, usefulValves, distances);
                bestFlow = Math.max(bestFlow, flow);
            }
        }
        return  bestFlow;
    }

    private static List<Distance> findDistances(List<Valve> usefulValves, List<Valve> valves) {

        List<Distance> distances = new ArrayList<>();

        for (Valve v : valves) {

            if (!usefulValves.contains(v)) {
                continue;
            }

            List<Valve> currentValves = Collections.singletonList(v);
            List<Valve> nextValves = new ArrayList<>();
            int distance = 0;

            distances.add(new Distance(v.name, v.name, 0));

            while (!currentValves.isEmpty()) {
                distance++;

                for (Valve current : currentValves) {
                    for (String e : current.neighbours) {
                        Valve exit = getValve(valves, e);

                        Distance currDistance = new Distance(current.name, exit.name, distance);
                        if (distances.contains(currDistance)) {
                            continue;
                        }

                        distances.add(currDistance);
                        nextValves.add(exit);

                    }
                }
                currentValves = nextValves;
                nextValves = new ArrayList<>();
            }
        }
        return distances;
    }

    private static List<Valve> parseInput(String file) throws IOException {

        List<Valve> valves = new ArrayList<>();

        for (String line : Files.readAllLines(Paths.get(file))) {

            String[] valveInfo = line.split(";")[0].split(" ");
            List<String> tunnelInfo = Arrays.stream(line.split(";")[1]
                    // Cuz im too lazy to use Regex
                    .replace("tunnels lead to valves ", "")
                    .replace("tunnel leads to valve ", "")
                    .trim()
                    .split(", ")
            ).toList();

            valves.add(new Valve(valveInfo[1], Integer.parseInt(valveInfo[4].split("=")[1]), tunnelInfo));

        }

        return valves;
    }

    private static Valve getValve(List<Valve> valves, String name) {
        return valves.stream().filter(v -> v.name.equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }
}


class Valve {

    final int flowRate;
    final String name;
    final List<String> neighbours;
    boolean isClosed = true;

    public Valve(String name, int flowRate, List<String> neighbours) {
        this.flowRate = flowRate;
        this.name = name;
        this.neighbours = neighbours;
    }
}

class Distance {
    final String start;
    final String end;
    final int distance;

    public Distance(String start, String end, int distance) {
        this.start = start;
        this.end = end;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return Objects.equals(start, distance.start) && Objects.equals(end, distance.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}