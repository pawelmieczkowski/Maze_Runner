package maze;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class MazeGenerator {
    private final Random rand = new Random();
    private final Scanner consoleScanner = new Scanner(System.in);
    private final int WIDTHENTRY;
    private final int HEIGHTHENTRY;
    private final int WIDTH;
    private final int HEIGHT;
    private int[][] matrix;
    private int exitRowIndex;
    private int exitColumnIndex;
    private final List<Vector2I> maze = new ArrayList<>();

    public MazeGenerator(int WIDTHENTRY, int HEIGHTHENTRY) {
        this.WIDTHENTRY = WIDTHENTRY;
        this.HEIGHTHENTRY = HEIGHTHENTRY;
        WIDTH = (int) Math.ceil(((double) WIDTHENTRY - 2) / 2);
        HEIGHT = (int) Math.ceil(((double) HEIGHTHENTRY - 2) / 2);

    }

    public MazeGenerator(int WIDTHENTRY, int HEIGHTHENTRY, int[][] matrix) {
        this.WIDTHENTRY = WIDTHENTRY;
        this.HEIGHTHENTRY = HEIGHTHENTRY;
        this.matrix = matrix;
        WIDTH = (int) Math.ceil(((double) WIDTHENTRY - 2) / 2);
        HEIGHT = (int) Math.ceil(((double) HEIGHTHENTRY - 2) / 2);

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][matrix[0].length - 1] == 0) {
                exitColumnIndex = matrix[0].length - 1;
                exitRowIndex = i;
            }
        }
    }


    public void transformToMatrix() {
        int[][] matrix = new int[HEIGHTHENTRY][WIDTHENTRY];

        //border
        for (int i = 0; i < matrix[0].length; i++) {
            matrix[0][i] = 1;
            matrix[matrix.length - 1][i] = 1;
        }
        //border
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][0] = 1;
            matrix[i][matrix[0].length - 1] = 1;
        }

        //make all nodes empty (paths) and make the rest walls
        for (int i = 1; i < matrix.length - 1; i++) {
            for (int j = 1; j < matrix[0].length - 1; j++) {
                if (i % 2 == 1 && j % 2 == 1) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = 1;
                }
            }
        }

        //add vertical paths to a matrix from generated mazeArray
        for (int i = 2; i < matrix.length - 2; i += 2) {
//            System.out.println("WIDTH=" + WIDTH);
            for (int j = 1; j < matrix[0].length - 1; j += 2) {
                int upperNode = (i - 1) / 2 * WIDTH + (j - 1) / 2;
                int lowerNode = (i + 1) / 2 * WIDTH + (j - 1) / 2;
//                System.out.println(upperNode + " : " + lowerNode);
                if (maze.contains(new Vector2I(upperNode, lowerNode))) {
                    matrix[i][j] = 0;
                }

            }
        }

        //add horizontal paths to a matrix from generated mazeArray
        for (int i = 1; i < matrix.length - 1; i += 2) {
//            System.out.println("---*");
            for (int j = 2; j < matrix[0].length; j += 2) {
                int leftNode = (i - 1) / 2 * WIDTH + (j - 2) / 2;
                int rightNode = (i - 1) / 2 * WIDTH + j / 2;
//                System.out.println(leftNode + " : " + rightNode);
                if (maze.contains(new Vector2I(leftNode, rightNode))) {
                    matrix[i][j] = 0;
                }

            }
        }

        //add entry
        matrix[1][0] = 0;

        //add exit
        for (int i = matrix.length - 2; i > 1; i--) {
            if (matrix[i][matrix[0].length - 2] == 0) {
                matrix[i][matrix[0].length - 1] = 0;
                exitRowIndex = i;
                exitColumnIndex = matrix[0].length - 1;
                break;
            }
        }
        this.matrix = matrix;
    }

    public void displayMaze() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    System.out.print("  ");
                } else {
                    System.out.print("\u2588\u2588");
                }
            }
            System.out.println();
        }
    }

    public void generate() {
        //vertex that were already visited
        List<Integer> visited = new ArrayList<>();
        //paths available to choose
        List<Vector2I> toVisit = new ArrayList<>();

        //this is where it starts
        visited.add(0);
        //initial paths that are possible to perform
        toVisit.add(new Vector2I(0, 1));
        //width here because it is 2d array represented as 1d array, so the width is actually just moving to row 1;
        toVisit.add(new Vector2I(0, WIDTH));

        while (toVisit.size() > 0) {
            int randomIndex = rand.nextInt(toVisit.size());
            Vector2I nextPath = toVisit.remove(randomIndex);

            //check if the tile was visited before (this will help avoid circle)
            if (visited.contains(nextPath.end))
                continue;

            //ensure the path goes from up to bottom and from left to right
            if (nextPath.start > nextPath.end)
                maze.add(new Vector2I(nextPath.end, nextPath.start));
            else
                maze.add(nextPath);

            visited.add(nextPath.end);

            int above = nextPath.end - WIDTH;
            if (above > 0 && !visited.contains(above))
                toVisit.add(new Vector2I(nextPath.end, above));

            //module here to check if we are not off the maze, this is 1d array, so it will go to previous row actually
            int left = nextPath.end - 1;
            if (left % WIDTH != WIDTH - 1 && !visited.contains(left))
                toVisit.add(new Vector2I(nextPath.end, left));

            int right = nextPath.end + 1;
            if (right % WIDTH != 0 && !visited.contains(right))
                toVisit.add(new Vector2I(nextPath.end, right));

            //width*heigth is the last index +1
            int below = nextPath.end + WIDTH;
            if (below < WIDTH * HEIGHT && !visited.contains(below))
                toVisit.add(new Vector2I(nextPath.end, below));
        }
        transformToMatrix();
    }

    public void saveMaze() {
        System.out.println("Enter path to store the maze");
        String path = consoleScanner.nextLine();
        File file = new File(path);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.println(WIDTHENTRY);
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    printWriter.print(matrix[i][j]);
                    printWriter.print(" ");
                }
                printWriter.print("\n");
            }

        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    public void findEscape() {
        int[][] escapeMatrix = copyMatrix();

        Stack<MatrixCell> stack = new Stack<>();

        stack.add(new MatrixCell(1, 0));

        while (true) {
            int rowIndex = stack.peek().getRowIndex();
            int columnIndex = stack.peek().getColumnIndex();
            System.out.println("row: " + rowIndex + ", column: " + columnIndex);
            //check if exit achieved
            if (rowIndex == exitRowIndex && columnIndex == exitColumnIndex) {
                printPath(stack);
                return;
            }

            //check right
            if (escapeMatrix[rowIndex][columnIndex + 1] == 0) {
                stack.add(new MatrixCell(rowIndex, columnIndex + 1));
                escapeMatrix[rowIndex][columnIndex] = -1;
            }
            //check up
            else if (escapeMatrix[rowIndex - 1][columnIndex] == 0) {
                stack.add(new MatrixCell(rowIndex - 1, columnIndex));
                escapeMatrix[rowIndex][columnIndex] = -1;
            }
            //check bottom
            else if (escapeMatrix[rowIndex + 1][columnIndex] == 0) {
                stack.add(new MatrixCell(rowIndex + 1, columnIndex));
                escapeMatrix[rowIndex][columnIndex] = -1;
                //check left
            } else if ((escapeMatrix[rowIndex][columnIndex - 1] == 0)) {
                stack.add(new MatrixCell(rowIndex, columnIndex - 1));
                escapeMatrix[rowIndex][columnIndex] = -1;
            } else {
                escapeMatrix[rowIndex][columnIndex] = -1;
                stack.pop();
                int reviveRow = stack.peek().getRowIndex();
                int reviveColumn = stack.pop().getColumnIndex();
                escapeMatrix[reviveRow][reviveColumn] = 0;
            }
        }


    }

    public void printPath(Stack<MatrixCell> stack) {
        int[][] escapeMatrix = copyMatrix();
        while (!stack.isEmpty()) {
            int rowIndex = stack.peek().getRowIndex();
            int columnIndex = stack.pop().getColumnIndex();
            escapeMatrix[rowIndex][columnIndex] = 2;
        }

        for (int i = 0; i < escapeMatrix.length; i++) {
            for (int j = 0; j < escapeMatrix[0].length; j++) {
                if (escapeMatrix[i][j] == 0) {
                    System.out.print("  ");
                } else if (escapeMatrix[i][j] == 2) {
                    System.out.print("//");
                } else {
                    System.out.print("\u2588\u2588");
                }
            }
            System.out.println();
        }
    }

    public int[][] copyMatrix() {
        int[][] copy = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                copy[i][j] = matrix[i][j];
            }
        }
        return copy;
    }
}