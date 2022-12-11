import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class main {

    public static void main(String[] args) throws Exception {

        Monkey monkey0 = new Monkey(0, Arrays.asList(85, 79, 63, 72), Operation.MULTIPLY, "17", 2, 2, 6);
        Monkey monkey1 = new Monkey(1, Arrays.asList(53, 94, 65, 81, 93, 73, 57, 92), Operation.MULTIPLY, "old", 7, 0, 2);
        Monkey monkey2 = new Monkey(2, Arrays.asList(62, 63), Operation.ADD, "7", 13, 7, 6);
        Monkey monkey3 = new Monkey(3, Arrays.asList(57, 92, 56), Operation.ADD, "4", 5, 4, 5);
        Monkey monkey4 = new Monkey(4, Arrays.asList(67), Operation.ADD, "5", 3, 1, 5);
        Monkey monkey5 = new Monkey(5, Arrays.asList(85, 56, 66, 72, 57, 99), Operation.ADD, "6", 19, 1, 0);
        Monkey monkey6 = new Monkey(6, Arrays.asList(86, 65, 98, 97, 69), Operation.MULTIPLY, "13", 11, 3, 7);
        Monkey monkey7 = new Monkey(7, Arrays.asList(87, 68, 92, 66, 91, 50, 68), Operation.ADD, "2", 17, 4, 3);

        List<Monkey> monkeyList = List.of(monkey0, monkey1, monkey2, monkey3, monkey4, monkey5, monkey6, monkey7);

        // Test data from website
//        Monkey monkey0 = new Monkey(0,Arrays.asList(79, 98), Operation.MULTIPLY, "19", 23, 2, 3);
//        Monkey monkey1 = new Monkey(1,Arrays.asList(54, 65, 75, 74), Operation.ADD, "6", 19,2, 0);
//        Monkey monkey2 = new Monkey(2,Arrays.asList(79, 60, 97), Operation.MULTIPLY, "old", 13, 1, 3);
//        Monkey monkey3 = new Monkey(3,Arrays.asList(74), Operation.ADD, "3", 17, 0, 1);

//        List<Monkey> monkeyList = List.of(monkey0, monkey1, monkey2, monkey3);

        int rounds = 20;
        Map<Integer, Long> inspectionCount = new HashMap<>();

        for (int i = 1; i <= rounds; i++) {

            for (Monkey monkey : monkeyList) {

                System.out.printf("Round %s - Monkey %s's turn%n", i, monkey.getNumber());

                for (Item item : monkey.getItems()) {

                    System.out.printf("%-2sMonkey inspects item with worry level %s%n", " ", item.worryLevel);

                    monkey.inspect(item);

                    Long count = inspectionCount.getOrDefault(monkey.getNumber(), 0L);
                    inspectionCount.put(monkey.getNumber(), ++count);

                    monkey.relief(item);
                    monkey.throwItem(item, monkeyList);

                }

                // clean monkey's inventory cuz it threw everything away, can't do this during iterating,
                // because of ConcurrentModification Exception
                monkey.getItems().clear();
            }
        }

        List<Long> inspections = inspectionCount.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());

        Long monkeyBusiness = inspections.get(0) * inspections.get(1);

        System.out.printf("Total amount of monkey business: %s%n", monkeyBusiness);
    }
}

class Monkey {

    private final int number;
    private final List<Item> items;
    private final Operation operation;
    private final String operationValue;
    private final int divisibleValue;
    private final int monkeyTrue;
    private final int monkeyFalse;

    public Monkey(int number, List<Integer> items, Operation operation, String operationValue, int divisibleValue, int monkeyTrue, int monkeyFalse) {
        this.number = number;
        this.items = items.stream().map(Item::new).collect(Collectors.toList());
        this.operation = operation;
        this.operationValue = operationValue;
        this.divisibleValue = divisibleValue;
        this.monkeyTrue = monkeyTrue;
        this.monkeyFalse = monkeyFalse;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getNumber() {
        return number;
    }

    public void inspect(Item item) {
        int value;

        if (operationValue.equals("old")) {
            value = item.getWorryLevel();
        } else {
            value = Integer.parseInt(operationValue);
        }

        // do something with value down here
        switch (operation) {
            case ADD -> {
                item.worryLevel += value;
                System.out.printf("%-4sWorry level increased by %s to %s%n", " ", value, item.worryLevel);
            }
            case MULTIPLY -> {
                item.worryLevel *= value;
                System.out.printf("%-4sWorry level increased by %s to %s%n", " ", operationValue, item.worryLevel);
            }
            case SUBTRACT -> {
                System.out.printf("Subtract %s%n", operationValue);
            }
            case DIVISION -> {
                System.out.printf("Division %s%n", operationValue);
            }
            default -> {
                System.out.printf("Unknown operation: %s%n", operation);
            }
        }
    }

    public void relief(Item item) {
        item.worryLevel = (int) Math.floor(item.worryLevel / 3d);
        System.out.printf("%-4sMonkey gets bored with item. Worry level is divided by %s to %s.%n", " ", "3", item.worryLevel);
    }

    public void throwItem(Item item, List<Monkey> monkeyList) {

        if (item.worryLevel % this.divisibleValue == 0) {
            System.out.printf("%-4sCurrent worry level is divisible by %s.%n", " ", this.divisibleValue);
            System.out.printf("%-4sItem with worry level %s is thrown to monkey %s.%n", " ", item.worryLevel, this.monkeyTrue);
            monkeyList.get(this.monkeyTrue).items.add(item);
        } else {
            System.out.printf("%-4sCurrent worry level is not divisible by %s.%n", " ", this.divisibleValue);
            System.out.printf("%-4sItem with worry level %s is thrown to monkey %s.%n", " ", item.worryLevel, this.monkeyFalse);
            monkeyList.get(this.monkeyFalse).items.add(item);
        }
    }
}

class Item {

    public Integer worryLevel;

    public Item(Integer worryLevel) {
        this.worryLevel = worryLevel;
    }

    public Integer getWorryLevel() {
        return worryLevel;
    }

    public void setWorryLevel(Integer worryLevel) {
        this.worryLevel = worryLevel;
    }
}

enum Operation {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVISION
}


