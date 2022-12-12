import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class main {

    public static void main(String[] args) throws Exception {

        String file = "input.txt";

        List<String> lines = Files.readAllLines(Paths.get(file));

        // Puzzle 1
        Graph graph_puzzle1 = new Graph();
        List<List<Node>> nodeList_puzzle1 = parseHeightMap(lines, graph_puzzle1);
        determineAdjacentNodes(nodeList_puzzle1, true);
        graph_puzzle1.setNodeList(nodeList_puzzle1.stream().flatMap(List::stream).collect(Collectors.toList()));

        graph_puzzle1 = calculateShortestPathFromSource(graph_puzzle1, graph_puzzle1.getRootNode());

        System.out.printf("Puzzle 1 steps required: %s%n", graph_puzzle1.getDestNode().getShortestPath().size());

        // Puzzle 2
        Graph graph_puzzle2 = new Graph();
        List<List<Node>> nodeList_puzzle2 = parseHeightMap(lines, graph_puzzle2);
        determineAdjacentNodes(nodeList_puzzle2, false);
        graph_puzzle2.setNodeList(nodeList_puzzle2.stream().flatMap(List::stream).collect(Collectors.toList()));

        graph_puzzle2 = calculateShortestPathFromSource(graph_puzzle2, graph_puzzle2.getDestNode());

        Node destNode = graph_puzzle2.nodeList.stream()
                .filter(n -> n.getName().equals("a"))
                .filter(n -> n.getShortestPath().size() != 0)
                .min(Comparator.comparing(node -> node.getShortestPath().size())).get();

        System.out.printf("Puzzle 2 steps required: %s%n", destNode.getShortestPath().size());
    }


    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node : unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private static void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeight, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }


    public static List<List<Node>> parseHeightMap(List<String> lines, Graph graph) {
        List<List<Node>> nodeMap = new ArrayList<>();
        for (String line : lines) {
            List<Node> nodeRow = new ArrayList<>();
            for (char step : line.toCharArray()) {
                Node node = new Node();
                // Start node
                if (step == 'S') {
                    node.setHeight(0);
                    graph.setRootNode(node);
                } else if (step == 'E') {
                    node.setHeight(25);
                    graph.setDestNode(node);
                } else {
                    node.setHeight(step - 97);
                }
                node.setName(String.valueOf(step));
                nodeRow.add(node);
            }
            nodeMap.add(nodeRow);
        }
        return nodeMap;
    }

    private static void determineAdjacentNodes(List<List<Node>> nodeList, boolean isAscend) {
        for (int y = 0; y < nodeList.size(); y++) {
            for (int x = 0; x < nodeList.get(y).size(); x++) {

                // Check left
                if (isValidNode(x, y, x - 1, y, nodeList, isAscend)) {
                    nodeList.get(y).get(x).addAdjacentNode(nodeList.get(y).get(x - 1), 1);
                }
                // Check right
                if (isValidNode(x, y, x + 1, y, nodeList, isAscend)) {
                    nodeList.get(y).get(x).addAdjacentNode(nodeList.get(y).get(x + 1), 1);
                }
                // Check up
                if (isValidNode(x, y, x, y - 1, nodeList, isAscend)) {
                    nodeList.get(y).get(x).addAdjacentNode(nodeList.get(y - 1).get(x), 1);
                }
                // Check down
                if (isValidNode(x, y, x, y + 1, nodeList, isAscend)) {
                    nodeList.get(y).get(x).addAdjacentNode(nodeList.get(y + 1).get(x), 1);
                }
            }
        }
    }

    public static boolean isValidNode(int x, int y, int destX, int destY, List<List<Node>> nodes, boolean isAscend) {
        // Puzzle 1
        if (isAscend) {
            return (destY >= 0 && destX >= 0
                    && destY < nodes.size()
                    && destX < nodes.get(destY).size()
                    && (nodes.get(destY).get(destX).getHeight() <= nodes.get(y).get(x).getHeight() + 1));
        } else {
            // Puzzle 2
            return (destY >= 0 && destX >= 0
                    && destY < nodes.size()
                    && destX < nodes.get(destY).size()
                    && (nodes.get(destY).get(destX).getHeight() >= nodes.get(y).get(x).getHeight() - 1));
        }
    }
}

class Graph {

    Node rootNode;
    Node destNode;
    List<Node> nodeList = new ArrayList<>();

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getDestNode() {
        return destNode;
    }

    public void setDestNode(Node destNode) {
        this.destNode = destNode;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void addNode(Node newNode) {
        this.nodeList.add(newNode);
    }
}

class Node {

    private String name;
    private int height = 0;
    private Integer distance = Integer.MAX_VALUE;

    private List<Node> shortestPath = new LinkedList<>();
    private Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void setHeight(int height) {
        this.height = height;
    }

    public void addAdjacentNode(Node node, int distance) {
        this.adjacentNodes.put(node, distance);
    }

    public Map<Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public int getHeight() {
        return height;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }
}
