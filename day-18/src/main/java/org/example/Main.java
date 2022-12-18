package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {

        String file = "input.txt";

        int gridsize = 20;
        Cube[][][] lavaGrid = parseInput(file, gridsize);

        // Puzzle 1
        int surfaceArea = countSurfaceArea(lavaGrid);
        System.out.printf("Total surface area: %s%n", surfaceArea);

        // Puzzle 2
        floodFill(lavaGrid, 0, 0, 0);
        int exteriorSurfaceAre = countExteriorSurfaceArea(lavaGrid);
        System.out.printf("Total exterior surface area: %s%n", exteriorSurfaceAre);
    }

    private static void floodFill(Cube[][][] lavaGrid, int x, int y, int z) {

        // Base case, prevent out of bounds
        if (x < 0 || x >= lavaGrid.length
                || y < 0 || y >= lavaGrid.length
                || z < 0 || z >= lavaGrid.length
                || lavaGrid[x][y][z] != null
        ) {
            return;
        }

        // Fill cube with water
        lavaGrid[x][y][z] = new WaterCube(x, y, z);

        // Spread
        if (isFree(lavaGrid, x - 1, y, z)) {
            floodFill(lavaGrid, x - 1, y, z);
        }
        if (isFree(lavaGrid, x + 1, y, z)) {
            floodFill(lavaGrid, x + 1, y, z);
        }
        if (isFree(lavaGrid, x, y - 1, z)) {
            floodFill(lavaGrid, x, y - 1, z);
        }
        if (isFree(lavaGrid, x, y + 1, z)) {
            floodFill(lavaGrid, x, y + 1, z);
        }
        if (isFree(lavaGrid, x, y, z - 1)) {
            floodFill(lavaGrid, x, y, z - 1);
        }
        if (isFree(lavaGrid, x, y, z + 1)) {
            floodFill(lavaGrid, x, y, z + 1);
        }
    }


    private static int countSurfaceArea(Cube[][][] lavaGrid) {

        int totalSurfaceArea = 0;
        int totalCount = 0;
        for (int x = 0; x < lavaGrid.length; x++) {
            for (int y = 0; y < lavaGrid.length; y++) {
                for (int z = 0; z < lavaGrid.length; z++) {

                    if (lavaGrid[x][y][z] == null) {
                        continue;
                    }

                    totalSurfaceArea += lavaGrid[x][y][z].countSurfaceArea(lavaGrid);
                    totalCount++;
                }
            }
        }

        System.out.printf("Total cubes counted: %s%n", totalCount);
        return totalSurfaceArea;
    }

    private static int countExteriorSurfaceArea(Cube[][][] lavaGrid) {

        int totalSurfaceArea = 0;

        for (int x = 0; x < lavaGrid.length; x++) {
            for (int y = 0; y < lavaGrid.length; y++) {
                for (int z = 0; z < lavaGrid.length; z++) {


                    if (lavaGrid[x][y][z] instanceof LavaCube){
                        totalSurfaceArea += lavaGrid[x][y][z].countExteriorSurfaceArea(lavaGrid);
                    }
                }
            }
        }

        System.out.printf("Total exterior surface area: %s%n", totalSurfaceArea);

        return totalSurfaceArea;
    }

    private static Cube[][][] parseInput(String file, int gridsize) throws Exception {

        Cube[][][] lavaGrid = new Cube[gridsize][gridsize][gridsize];

        for (String line : Files.readAllLines(Paths.get(file))) {

            String[] coords = line.split(",");

            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);

            Cube cube = new LavaCube(x, y, z);

            lavaGrid[x][y][z] = cube;
        }
        return lavaGrid;
    }


    public static boolean isFree(Cube[][][] grid, int x, int y, int z) {

        // This will go out of bounds
        if (x < 0 || x >= grid.length
                || y < 0 || y >= grid.length
                || z < 0 || z >= grid.length
        ) {
            return false;
        }

        return grid[x][y][z] == null;
    }
}

class Cube {

    final int x;
    final int y;
    final int z;

    public Cube(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int countSurfaceArea(Cube[][][] grid) {
        // Cubes have 6 sides
        int surfaceArea = 6;

        if (isOccupied(grid, this.x - 1, this.y, this.z)) {
            surfaceArea--;
        }

        if (isOccupied(grid, this.x + 1, this.y, this.z)) {
            surfaceArea--;
        }

        if (isOccupied(grid, this.x, this.y - 1, this.z)) {
            surfaceArea--;
        }

        if (isOccupied(grid, this.x, this.y + 1, this.z)) {
            surfaceArea--;
        }

        if (isOccupied(grid, this.x, this.y, this.z - 1)) {
            surfaceArea--;
        }

        if (isOccupied(grid, this.x, this.y, this.z + 1)) {
            surfaceArea--;
        }

        return surfaceArea;


    }

    public int countExteriorSurfaceArea(Cube[][][] grid) {
        // Cubes have 6 sides
        int surfaceArea = 6;

        if (isLavaOrAir(grid, this.x - 1, this.y, this.z)) {
            surfaceArea--;
        }

        if (isLavaOrAir(grid, this.x + 1, this.y, this.z)) {
            surfaceArea--;
        }

        if (isLavaOrAir(grid, this.x, this.y - 1, this.z)) {
            surfaceArea--;
        }

        if (isLavaOrAir(grid, this.x, this.y + 1, this.z)) {
            surfaceArea--;
        }

        if (isLavaOrAir(grid, this.x, this.y, this.z - 1)) {
            surfaceArea--;
        }

        if (isLavaOrAir(grid, this.x, this.y, this.z + 1)) {
            surfaceArea--;
        }
        return surfaceArea;
    }

    private boolean isOccupied(Cube[][][] grid, int x, int y, int z) {

        // This will go out of bounds
        if (x < 0 || x >= grid.length
                || y < 0 || y >= grid.length
                || z < 0 || z >= grid.length
        ) {
            return false;
        }

        return grid[x][y][z] != null;
    }

    private boolean isLavaOrAir(Cube[][][] grid, int x, int y, int z) {

        // This will go out of bounds
        if (x < 0 || x >= grid.length
                || y < 0 || y >= grid.length
                || z < 0 || z >= grid.length
        ) {
            return false;
        }

        return grid[x][y][z] instanceof LavaCube || grid[x][y][z] == null;
    }
}

class LavaCube extends Cube {

    public LavaCube(int x, int y, int z) {
        super(x, y, z);
    }
}

class WaterCube extends Cube {

    public WaterCube(int x, int y, int z) {
        super(x, y, z);
    }
}