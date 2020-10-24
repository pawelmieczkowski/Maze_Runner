package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GameMaze {
    boolean mazeExist = false;
    private final Scanner consoleScanner = new Scanner(System.in);
    MazeGenerator maze;

    public void play() {
        while (true) {
            printMenu();
            switch (consoleScanner.nextLine()) {
                case "1": {
                    generateMaze();
                    maze.displayMaze();
                    break;
                }
                case "2": {
                    loadMaze();
                    break;
                }
                case "3": {
                    if (!mazeExist) {
                        continue;
                    }
                    maze.saveMaze();
                    break;
                }
                case "4": {
                    if (!mazeExist) {
                        continue;
                    }
                    maze.displayMaze();
                    break;
                }
                case "5": {
                    if (!mazeExist) {
                        continue;
                    }
                    maze.findEscape();
                    break;
                }
                case "0": {
                    return;
                }
                default: {
                    System.out.println("Incorrect option. Please try again");
                    break;
                }
            }
        }

    }

    private void printMenu() {
        if (!mazeExist) {
            System.out.println("=== Menu ===\n" +
                    "1. Generate a new maze\n" +
                    "2. Load a maze\n" +
                    "0. Exit");
        } else {
            System.out.println("=== Menu ===\n" +
                    "1. Generate a new maze\n" +
                    "2. Load a maze\n" +
                    "3. Save the maze\n" +
                    "4. Display the maze\n" +
                    "5. Find the escape\n" +
                    "0. Exit");
        }
    }

    private void loadMaze() {
        System.out.println("Enter path to a file containing the maze");
        String path = consoleScanner.nextLine();
        File file = new File(path);
        try (Scanner scanner = new Scanner(file)) {
            try {
                int WIDTHENTRY = Integer.parseInt(scanner.nextLine());
                int[][] matrix = new int[WIDTHENTRY][WIDTHENTRY];
                int row = 0;
                while (scanner.hasNext()) {
                    String[] line = scanner.nextLine().split(" ");
                    for (int i = 0; i < WIDTHENTRY; i++) {
                        matrix[row][i] = Integer.parseInt(line[i]);
                    }
                    row++;
                }
                this.maze = new MazeGenerator(WIDTHENTRY, WIDTHENTRY, matrix);
                this.mazeExist = true;
            } catch (Error error) {
                System.out.println("Cannot load the maze. It has an invalid format");
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file " + path + " does not exist");
        }
    }

    private void generateMaze() {
        Scanner scanner = new Scanner(System.in);
        int height = scanner.nextInt();
        //turned into square mazes only
        //int width = scanner.nextInt();
        this.maze = new MazeGenerator(height, height);
        maze.generate();
        this.mazeExist = true;
    }

}
