import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws Exception {

        String file = "input.txt";

        HashSet<Beacon> beacons = new HashSet<>();
        List<Signal> signals = new ArrayList<>();

        parseInput(file, signals, beacons);

        // y=10 For test input
//        int y = 10;

        // For puzzle 1
        int y = 10;

//        countImpossiblePositionsAtY(signals, beacons, y);


//        int cap = 20;
        int cap = 4_000_000;

        long freq_multiplier = 4_000_000;
        Long freq = findDistressBeaconFrequency(signals, cap, freq_multiplier);

        System.out.printf("Distress beacon found with frequency: %s%n", freq);
    }

    private static Long findDistressBeaconFrequency(List<Signal> signals, long cap, long freqMultiplier) {

        long freq = -1L;
        yLoop:
        for (int y = 0; y <= cap; y++) {
            xLoop:
            for (int x = 0; x <= cap; x++) {

                for (Signal s : signals) {

                    int offsetY = Math.abs(y - s.y) - 1;
                    int offsetX = s.range - offsetY - 1;

                    // If current x-position is in-between the range of current signal, skip to end of range
                    if (s.x-offsetX <= x && s.x+offsetX >= x) {

                        x = s.x + offsetX;
                        continue xLoop;
                    }
                }

                freq = (x * freqMultiplier) + y;
                System.out.printf("No beacon found at x=%s, y=%s, freq=%s%n", x, y, freq);
            }
        }
        return freq;
    }

    private static void countImpossiblePositionsAtY(List<Signal> signals, Set<Beacon> beacons, int y) {

        List<Range> coveredRanges = new ArrayList<>();
        int overlap = 0;

        for (Signal s : signals) {

            int offsetY = Math.abs(y - s.y) - 1;
            int offsetX = s.range - offsetY - 1;

            if (offsetX > 0) {
                Range signalRange = new Range(s.x - offsetX, s.x + offsetX);
                coveredRanges.add(signalRange);
            }

            if (s.isSignalAtY(y)) {
                overlap++;
            }
        }

        for (Beacon b : beacons) {
            if (b.y == y) {
                overlap++;
            }
        }

        int lowestX = coveredRanges.stream()
                .mapToInt(Range::getLow)
                .min()
                .orElseThrow();

        int highestX = coveredRanges.stream()
                .mapToInt(Range::getHigh)
                .max()
                .orElseThrow();

        HashSet<Integer> occupiedX = new HashSet<>();

        for (Range r : coveredRanges) {
            occupiedX.addAll(r.toList());
        }

        System.out.printf("lowest: %s, highest: %s, covered: %s, overlap: %s%n", lowestX, highestX, occupiedX.size(), overlap);
    }

    private static List<Signal> parseInput(String file, List<Signal> signals, Set<Beacon> beacons) throws Exception {

        for (String line : Files.readAllLines(Paths.get(file))) {

            String sensor = line.split(":")[0].replace("Sensor at ", "");
            int sensorX = Integer.parseInt(sensor.split(", ")[0].replace("x=", "").trim());
            int sensorY = Integer.parseInt(sensor.split(", ")[1].replace("y=", "").trim());

            String beacon = line.split(":")[1].replace("closest beacon is at", "");
            int beaconX = Integer.parseInt(beacon.split(", ")[0].replace("x=", "").trim());
            int beaconY = Integer.parseInt(beacon.split(", ")[1].replace("y=", "").trim());


            int dx = Math.abs(sensorX - beaconX);
            int dy = Math.abs(sensorY - beaconY);

            int range = dx + dy;

            Beacon b = new Beacon(beaconX, beaconY);
            beacons.add(b);

            signals.add(new Signal(sensorX, sensorY, b, range));
            System.out.printf("Sensor x=%s, y=%s, connected to beacon x=%s, y=%s, with range %s%n", sensorX, sensorY, beaconX, beaconY, range);
        }

        return signals;
    }
}

class Signal {

    final int x;
    final int y;
    final Beacon closestBeacon;
    final int range;
    Map<Integer, Range> coverage = new HashMap<>();

    public Signal(int x, int y, Beacon beacon, int range) {
        this.x = x;
        this.y = y;
        this.closestBeacon = beacon;
        this.range = range;
    }

    public boolean isBeaconAtY(int y) {
        return this.closestBeacon.y == y;
    }

    public boolean isSignalAtY(int y) {
        return this.y == y;
    }

    public Map<Integer, Range> getCoverage() {
        return coverage;
    }

    public void setCoverageAtY(Integer yLevel, Range coverage) {
        this.coverage.put(yLevel, coverage);
    }
}

class Beacon {

    final int x;
    final int y;

    public Beacon(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beacon beacon = (Beacon) o;
        return x == beacon.x && y == beacon.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

class Range {
    private int low;
    private int high;

    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public boolean contains(int number) {
        return (number >= low && number <= high);
    }

    public int length() {
        return high - low;
    }

    public void extendStart(int low) {
        this.low = low;
    }

    public void extendEnd(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    public List<Integer> toList() {
        return IntStream
                .rangeClosed(this.low, this.high)
                .boxed()
                .collect(Collectors.toList());
    }

}