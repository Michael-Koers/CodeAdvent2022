import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class main {

    private static final int TOTAL_DISK_SPACE = 70_000_000;
    private static final int REQUIRED_DISK_SPACE = 30_000_000;

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("day-7/input.txt"));

        // root node
        Node rootNode = new Node("/", null);
        Node currNode = rootNode;
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(currNode);

        for (int i = 0; i < lines.size(); i++) {

            String[] cmd = lines.get(i).split(" ");

            switch (cmd[1]) {
                case "cd" -> {
                    switch (cmd[2]) {
                        case "/" -> currNode = rootNode;
                        case ".." -> currNode = currNode.getParent();
                        default -> currNode = currNode.getNodes()
                                .stream()
                                .filter(n -> n.getName().equals(cmd[2])).findFirst()
                                .get();
                    }
                }
                case "ls" -> {

                    List<String> dirNodes = lines
                            .stream()
                            .skip(i + 1)
                            .takeWhile(l -> !l.startsWith("$"))
                            .collect(Collectors.toList());

                    for (String dirNode : dirNodes) {

                        String[] dirNodeParts = dirNode.split(" ");

                        if (dirNodeParts[0].equals("dir")) {
                            Node newNode = new Node(dirNodeParts[1], currNode);
                            currNode.addNode(newNode);
                            nodeList.add(newNode);
                        } else {
                            int fileSize = Integer.parseInt(dirNodeParts[0]);
                            currNode.addSize(fileSize);
                        }
                    }

                    i += dirNodes.size();

                }
            }
        }

        // part 1
        int totalSizeOfSmallDirs = nodeList
                .stream()
                .filter(n -> n.getDirSize() <= 100000)
                .mapToInt(Node::getDirSize).sum();

        System.out.printf("Total size: %s", totalSizeOfSmallDirs);

        // part 2
        int remainingSpace = TOTAL_DISK_SPACE - rootNode.getDirSize();
        int spaceToBeCleared = REQUIRED_DISK_SPACE - remainingSpace;

        Node dirToDelete = nodeList.stream()
                .filter(n -> n.getDirSize() >= spaceToBeCleared)
                .sorted(Comparator.comparing(Node::getDirSize))
                .findFirst().get();

        System.out.printf("Smallest dir size to delete: %d", dirToDelete.getDirSize());
    }
}

class Node {

    private final String name;
    private final Node parent;
    private final List<Node> nodes = new ArrayList<>();

    private int size = 0;

    public Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
    }

    public int getSize() {
        return size;
    }

    public void addSize(int size) {
        this.size += size;
    }

    public Node getParent() {
        if (parent == null) {
            return this;
        }
        return parent;
    }

    public Integer getDirSize() {
        return size + nodes.stream().mapToInt(Node::getDirSize).sum();
    }

    public void addNode(Node childNode) {
        this.nodes.add(childNode);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return name;
    }
}