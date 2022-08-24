package tictactoe;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final char playerOne = 'X';
    private static final char playerTwo = 'O';

    private enum GameState {
        NOT_FINISHED,
        DRAW,
        X_WINS,
        O_WINS,
        IMPOSSIBLE
    }

    private static final char[][] gamegrid = new char[3][3];
    private static final String[] gameboard = {
            "---------",
            null,
            null,
            null,
            "---------"
    };

    public static int getCharCount(char n) {

        int count = 0;
        for (char[] row : gamegrid) {
            for (char c : row) {
                if (c == n) {
                    count++;
                }
            }
        }

        return count;

    }

    private static char getGridOccupant(int x, int y) {
        return gamegrid[x - 1][y - 1];
    }

    private static boolean addGridOccupant(int x, int y, char c) {

        char occupant = getGridOccupant(x, y);
        if (occupant == '_' || occupant == '\0') {
            gamegrid[x - 1][y - 1] = c;
            return true;
        }

        return false;

    }

    public static String checkWinner(int rowSum, int colSum) {

        int threeX = 264;
        int threeO = 237;

        if (rowSum == threeX || colSum == threeX) {
            return "X";
        } else if (rowSum == threeO || colSum == threeO) {
            return "O";
        } else {
            return null;
        }

    }

    public static int sumRow(int i) {
        return getGridOccupant(i, 1) + getGridOccupant(i, 2) + getGridOccupant(i, 3);
    }

    public static int sumCol(int i) {
        return getGridOccupant(1, i) + getGridOccupant(2, i) + getGridOccupant(3, i);
    }

    public static int sumDiag(int i) {

        if (i == 1) {
            return getGridOccupant(i, 1) + getGridOccupant(i + 1, 2) + getGridOccupant(i + 2, 3);
        } else if (i == 3) {
            return getGridOccupant(i, 1) + getGridOccupant(i - 1, 2) + getGridOccupant(i - 2, 3);
        }
        return 0;

    }

    public static ArrayList<String> findWinner() {

        ArrayList<String> winner = new ArrayList<>();

        for (int row = 1; row <= 3; row++) {

            int colSum = 0;
            int rowSum = sumRow(row);
            int diagSum = sumDiag(row);

            if (row == 1) {

                for (int col = 1; col <= 3; col++) {

                    colSum = sumCol(col);
                    String w = checkWinner(rowSum, colSum);
                    if (w != null && !winner.contains(w)) {
                        winner.add(w);
                    }

                }

            }

            String w = checkWinner(rowSum, diagSum);
            if (w == null) {
                w = checkWinner(rowSum, colSum);
            }

            if (!winner.contains(w)) {
                winner.add(w);
            }

        }

        return winner;

    }

    public static GameState getGameState() {

        ArrayList<String> winner = findWinner();
        winner = (ArrayList<String>) winner.stream().filter(Objects::nonNull).collect(Collectors.toList());
//        System.out.println(winner);

        if (Math.abs(getCharCount('X') - getCharCount('O')) > 1 || winner.size() > 1) {
            return GameState.IMPOSSIBLE;
        } else if (winner.contains("X")) {
            return GameState.X_WINS;
        } else if (winner.contains("O")) {
            return GameState.O_WINS;
        } else if (getCharCount('_') > 0) {
            return GameState.NOT_FINISHED;
        } else {
            return GameState.DRAW;
        }

    }

    public static void printGameState() {

        switch (getGameState()) {

            case DRAW:
                System.out.println("Draw");
                break;

            case NOT_FINISHED:
                System.out.println("Game not finished");
                break;

            case IMPOSSIBLE:
                System.out.println("Impossible");
                break;

            case O_WINS:
                System.out.println("O wins");
                break;

            case X_WINS:
                System.out.println("X wins");
                break;
        }
    }

    public static void printGameBoard() {

        for (int r = 1; r <= 3; r++) {

            String l = "|";

            for (int c = 0; c < 3; c++) {
                l = String.format("%s %s", l, gamegrid[r - 1][c]);
            }

            l = String.format("%s %s", l, "|");
            gameboard[r] = l;

        }

        for (String l : gameboard) {
            System.out.println(l);
        }

    }

    private static void initializeGameBoard(char[] initialState) {

        int p = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                char thisChar;

                if (initialState == null || p > initialState.length - 1) {
                    thisChar = '_';
                } else {

                    thisChar = initialState[p];
                    if (thisChar != 'X' && thisChar != 'O' && thisChar != '_') {
                        thisChar = '_';
                    }

                }

                p++;
                addGridOccupant(r + 1, c + 1, thisChar);

            }
        }

    }

    private static char[] getInitialBoardFromUserInput(InputStream in) {

        Scanner scanner = new Scanner(in);

        boolean valid = false;
        String inputStr;
        do {

            inputStr = scanner.nextLine();
            if (inputStr == null || inputStr.length() != 9) {
                System.out.println("incorrect grid length! Try again.");
                continue;
            }

            valid = true;

        } while (!valid);

        return inputStr.toCharArray();

    }

    private static void getNextMoveFromUserInput(InputStream in, char player) {

        Scanner scanner = new Scanner(in);
        boolean valid = false;
        String inputStr;

        do {

            inputStr = scanner.nextLine();

            if (inputStr.length() != 3) {
                System.out.println("Input should be two digits separated by a space!");
                continue;
            }

            int x, y;

            try {

                String[] spl = inputStr.split(" ");
                x = Integer.parseInt(spl[0]);
                y = Integer.parseInt(spl[1]);

            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                continue;
            }

            if ((x < 1 || x > 3) || (y < 1 || y > 3)) {
                System.out.println("Coordinates should be from 1 to 3!");
                continue;
            }

            if (!addGridOccupant(x, y, player)) {
                System.out.println("This cell is occupied! Choose another one!");
                continue;
            }
            valid = true;

        } while (!valid);

    }

    public static void main(String[] args) {

        char[] gg;
        gg = new char[]{
                '_', '_', '_',
                '_', '_', '_',
                '_', '_', '_'
        };
//        gg = getInitialBoardFromUserInput(System.in);

        initializeGameBoard(gg);
        boolean playerOneTurn = true;
        do {

            char currentPlayer = playerOneTurn ? playerOne : playerTwo;

            printGameBoard();
            getNextMoveFromUserInput(System.in, currentPlayer);
            printGameBoard();

            playerOneTurn = !playerOneTurn;

        } while (getGameState() == GameState.NOT_FINISHED);

        printGameState();

    }

}
